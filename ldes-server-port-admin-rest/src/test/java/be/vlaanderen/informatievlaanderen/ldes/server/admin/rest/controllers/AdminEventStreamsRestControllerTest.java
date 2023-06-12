package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.IsIsomorphic;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamListHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.EventStreamValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.FragmentationConfigExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.RetentionModelExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.jena.riot.WebContent.contentTypeTurtle;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@ContextConfiguration(classes = { AppConfig.class, AdminEventStreamsRestController.class, ModelConverter.class,
		EventStreamListHttpConverter.class, EventStreamHttpConverter.class, EventStreamResponseConverterImpl.class,
		ViewSpecificationConverter.class, PrefixAdderImpl.class, EventStreamValidator.class,
		AdminRestResponseEntityExceptionHandler.class, RetentionModelExtractor.class,
		FragmentationConfigExtractor.class })
class AdminEventStreamsRestControllerTest {
	private static final String COLLECTION = "name1";
	@MockBean
	private EventStreamService eventStreamService;

	@Autowired
	private MockMvc mockMvc;

	@Nested
	class GetEventStreamList {
		private List<EventStreamResponse> eventStreams;

		@BeforeEach
		void initEventStreams() throws URISyntaxException {
			Model shape = readModelFromFile("example-shape.ttl");
			FragmentationConfig fragmentationConfig = new FragmentationConfig();
			fragmentationConfig.setName("ExampleFragmentation");
			fragmentationConfig.setConfig(Map.of("property", "ldes:propertyPath"));
			ViewSpecification singleView = new ViewSpecification(
					new ViewName("name2", "view1"),
					List.of(),
					List.of(fragmentationConfig));
			List<ViewSpecification> views = List.of(
					new ViewSpecification(
							new ViewName(COLLECTION, "view2"),
							List.of(),
							List.of(fragmentationConfig)),
					new ViewSpecification(
							new ViewName(COLLECTION, "view3"),
							List.of(),
							List.of(fragmentationConfig)));

			eventStreams = List.of(
					new EventStreamResponse(COLLECTION, "http://purl.org/dc/terms/created",
							"http://purl.org/dc/terms/isVersionOf",
							"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder", views, shape),
					new EventStreamResponse("name2", "http://purl.org/dc/terms/created",
							"http://purl.org/dc/terms/isVersionOf",
							"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder", List.of(singleView),
							shape));
		}

		@Test
		void when_StreamsPresent_then_StreamsAreReturned() throws Exception {
			Model expectedEventStreamsModel = readModelFromFile("multiple-ldes.ttl");

			when(eventStreamService.retrieveAllEventStreams()).thenReturn(eventStreams);

			mockMvc.perform(get("/admin/api/v1/eventstreams").accept(contentTypeTurtle))
					.andExpect(status().isOk())
					.andExpect(IsIsomorphic.with(expectedEventStreamsModel));

			verify(eventStreamService).retrieveAllEventStreams();
		}
	}

	@Nested
	class GetSingleEventStream {
		@Test
		void when_StreamPresent_Then_StreamIsReturned() throws Exception {
			Model model = readModelFromFile("ldes-1.ttl");
			Model shape = readModelFromFile("example-shape.ttl");
			EventStreamResponse eventStream = new EventStreamResponse("name1", "http://purl.org/dc/terms/created",
					"http://purl.org/dc/terms/isVersionOf",
					"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder",
					List.of(), shape);

			when(eventStreamService.retrieveEventStream(COLLECTION)).thenReturn(eventStream);

			mockMvc.perform(get("/admin/api/v1/eventstreams/" + COLLECTION).accept(contentTypeTurtle))
					.andExpect(status().isOk())
					.andExpect(IsIsomorphic.with(model));

			verify(eventStreamService).retrieveEventStream(COLLECTION);
		}

		@Test
		void when_StreamNotPresent_Then_Returned404() throws Exception {
			when(eventStreamService.retrieveEventStream(COLLECTION))
					.thenThrow(new MissingEventStreamException(COLLECTION));

			mockMvc.perform(get("/admin/api/v1/eventstreams/" + COLLECTION).accept(contentTypeTurtle))
					.andExpect(status().isNotFound());

			verify(eventStreamService).retrieveEventStream(COLLECTION);
		}
	}

	@Nested
	class CreateEventStream {
		@Test
		void when_eventStreamModelIsPut_then_eventStreamIsSaved_and_status200IsExpected() throws Exception {
			final Model expectedModel = readModelFromFile("ldes-1.ttl");
			final Model shape = readModelFromFile("example-shape.ttl");

			EventStreamResponse eventStreamResponse = new EventStreamResponse(
					"name1",
					"http://purl.org/dc/terms/created",
					"http://purl.org/dc/terms/isVersionOf",
					"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder",
					List.of(), shape);

			when(eventStreamService.createEventStream(any(EventStreamResponse.class))).thenReturn(eventStreamResponse);

			mockMvc.perform(post("/admin/api/v1/eventstreams")
					.accept(contentTypeTurtle)
					.content(readDataFromFile("ldes-1.ttl"))
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isCreated())
					.andExpect(IsIsomorphic.with(expectedModel));

			verify(eventStreamService).createEventStream(any(EventStreamResponse.class));
		}

		@Test
		void when_ModelWithoutType_Then_ReturnedBadRequest() throws Exception {
			mockMvc.perform(post("/admin/api/v1/eventstreams")
					.content(readDataFromFile("ldes-without-type.ttl"))
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(eventStreamService);
		}

		@Test
		void when_MalformedModelInRequestBody_Then_ReturnedBadRequest() throws Exception {
			mockMvc.perform(post("/admin/api/v1/eventstreams")
					.content(readDataFromFile("malformed-ldes.ttl"))
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(eventStreamService);
		}
	}

	@Nested
	class DeleteEventStream {
		@Test
		void when_collectionExists_and_triesToDelete_then_expectStatus200() throws Exception {
			mockMvc.perform(delete("/admin/api/v1/eventstreams/name1"))
					.andExpect(status().isOk());

			verify(eventStreamService).deleteEventStream(COLLECTION);
		}

		@Test
		void when_deleteNotExistingCollection_then_expectStatus404() throws Exception {
			doThrow(MissingEventStreamException.class).when(eventStreamService).deleteEventStream(COLLECTION);

			mockMvc.perform(delete("/admin/api/v1/eventstreams/name1"))
					.andExpect(status().isNotFound());

			verify(eventStreamService).deleteEventStream(COLLECTION);
		}
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
