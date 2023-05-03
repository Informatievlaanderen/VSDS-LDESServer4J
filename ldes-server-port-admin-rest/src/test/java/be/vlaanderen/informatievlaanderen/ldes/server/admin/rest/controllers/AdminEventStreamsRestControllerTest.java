package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.AdminWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@ContextConfiguration(classes = { AdminEventStreamsRestController.class, AdminWebConfig.class,
		AdminRestResponseEntityExceptionHandler.class })
class AdminEventStreamsRestControllerTest {
	@MockBean
	private EventStreamService eventStreamService;

	@MockBean
	private ShaclShapeService shaclShapeService;

	@MockBean
	private ViewService viewService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void when_StreamsPresent_then_StreamsAreReturned() throws Exception {
		String collectionName = "name1";
		// TODO: add views to the turtle file for testing
		Model expectedEventStreamsModel = readModelFromFile("multiple-ldes.ttl");
		Model shape = readModelFromFile("example-shape.ttl");
		List<EventStream> eventStreams = List.of(
				new EventStream("name1", "http://purl.org/dc/terms/created",
						"http://purl.org/dc/terms/isVersionOf"),
				new EventStream("name2", "http://purl.org/dc/terms/created",
						"http://purl.org/dc/terms/isVersionOf"));

		when(eventStreamService.retrieveAllEventStreams()).thenReturn(eventStreams);
		when(shaclShapeService.retrieveShaclShape(collectionName)).thenReturn(new ShaclShape("name1", shape));
		when(shaclShapeService.retrieveShaclShape("name2")).thenReturn(new ShaclShape("name2", shape));
		when(viewService.getViewsByCollectionName(collectionName)).thenReturn(List.of());

		mockMvc.perform(get("/admin/api/v1/eventstreams"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(IsIsomorphic.with(expectedEventStreamsModel));

		InOrder inOrder = inOrder(eventStreamService, shaclShapeService, viewService);
		inOrder.verify(eventStreamService).retrieveAllEventStreams();
		inOrder.verify(viewService).getViewsByCollectionName(collectionName);
		inOrder.verify(shaclShapeService).retrieveShaclShape(collectionName);
		inOrder.verify(viewService).getViewsByCollectionName("name2");
		inOrder.verify(shaclShapeService).retrieveShaclShape("name2");
		inOrder.verifyNoMoreInteractions();

	}

	@Test
	void when_StreamPresent_Then_StreamIsReturned() throws Exception {
		String collectionName = "name1";
		Model model = readModelFromFile("ldes-1.ttl");
		Model shape = readModelFromFile("example-shape.ttl");
		EventStream eventStream = new EventStream("name1", "http://purl.org/dc/terms/created",
				"http://purl.org/dc/terms/isVersionOf");

		when(eventStreamService.retrieveEventStream(collectionName)).thenReturn(eventStream);
		when(shaclShapeService.retrieveShaclShape(collectionName)).thenReturn(new ShaclShape("name1", shape));
		when(viewService.getViewsByCollectionName(collectionName)).thenReturn(List.of());

		mockMvc.perform(get("/admin/api/v1/eventstreams/" + collectionName))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(IsIsomorphic.with(model));

		InOrder inOrder = inOrder(eventStreamService, shaclShapeService, viewService);
		inOrder.verify(eventStreamService).retrieveEventStream(collectionName);
		inOrder.verify(viewService).getViewsByCollectionName(collectionName);
		inOrder.verify(shaclShapeService).retrieveShaclShape(collectionName);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_StreamNotPresent_Then_Returned404() throws Exception {
		String collectionName = "name1";

		when(eventStreamService.retrieveEventStream(collectionName))
				.thenThrow(new MissingEventStreamException(collectionName));

		mockMvc.perform(get("/admin/api/v1/eventstreams/" + collectionName))
				.andDo(print())
				.andExpect(status().isNotFound());

		verify(eventStreamService).retrieveEventStream(collectionName);
		verifyNoInteractions(shaclShapeService, viewService);
	}

	@Test
	void when_eventStreamModelIsPut_then_eventStreamIsSaved_and_status200IsExpected() throws Exception {
		// TODO: add views to turtle file for testing and delete 'never()' in
		// viewService verification
		final Model expectedModel = readModelFromFile("ldes-1.ttl");

		mockMvc.perform(put("/admin/api/v1/eventstreams")
				.content(readDataFromFile("ldes-1.ttl"))
				.contentType(Lang.TURTLE.getHeaderString()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(IsIsomorphic.with(expectedModel));

		InOrder inOrder = inOrder(eventStreamService, shaclShapeService, viewService);
		inOrder.verify(eventStreamService).saveEventStream(any(EventStream.class));
		inOrder.verify(viewService, never()).addView(any(ViewSpecification.class));
		inOrder.verify(shaclShapeService).updateShaclShape(any(ShaclShape.class));
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_ModelWithoutType_Then_ReturnedBadRequest() throws Exception {
		mockMvc.perform(put("/admin/api/v1/eventstreams")
				.content(readDataFromFile("ldes-without-type.ttl"))
				.contentType(Lang.TURTLE.getHeaderString()))
				.andDo(print())
				.andExpect(status().isBadRequest());

		verifyNoInteractions(eventStreamService, shaclShapeService, viewService);
	}

	@Test
	void when_MalformedModelInRequestBody_Then_ReturnedBadRequest() throws Exception {
		var request = put("/admin/api/v1/eventstreams")
				.content(readDataFromFile("malformed-ldes.ttl"))
				.contentType(Lang.TURTLE.getHeaderString());
		mockMvc.perform(request).andDo(print());
		mockMvc.perform(request)
				.andExpect(status().isBadRequest());

		verifyNoInteractions(eventStreamService, shaclShapeService, viewService);
	}

	@Test
	void when_collectionExists_and_triesToDelete_then_expectStatus200() throws Exception {
		String collectionName = "name1";

		List<ViewSpecification> views = Stream.of("name1/view1", "name1/view2")
				.map(ViewName::fromString)
				.map(viewName -> {
					ViewSpecification view = new ViewSpecification();
					view.setName(viewName);
					return view;
				})
				.toList();

		when(viewService.getViewsByCollectionName(collectionName)).thenReturn(views);

		mockMvc.perform(delete("/admin/api/v1/eventstreams/name1"))
				.andDo(print())
				.andExpect(status().isOk());

		InOrder inOrder = inOrder(eventStreamService, viewService, shaclShapeService);
		inOrder.verify(eventStreamService).deleteEventStream(collectionName);
		inOrder.verify(viewService).deleteViewByViewName(ViewName.fromString("name1/view1"));
		inOrder.verify(viewService).deleteViewByViewName(ViewName.fromString("name1/view2"));
		inOrder.verify(shaclShapeService).deleteShaclShape(collectionName);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

	private String readDataFromFile(String fileName)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		Path path = Paths.get(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		return Files.lines(path).collect(Collectors.joining());
	}

}