package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.ValidatorsConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.IsIsomorphic;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamListHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.HttpModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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

import static org.apache.jena.riot.WebContent.contentTypeNQuads;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({"test", "rest"})
@ContextConfiguration(classes = {AdminEventStreamsRestController.class, HttpModelConverter.class,
		EventStreamListHttpConverter.class, EventStreamHttpConverter.class, EventStreamConverterImpl.class,
		ViewSpecificationConverter.class, PrefixAdderImpl.class, ValidatorsConfig.class,
		AdminRestResponseEntityExceptionHandler.class, RetentionModelExtractor.class,
		FragmentationConfigExtractor.class, PrefixConstructor.class, RdfModelConverter.class})
class AdminEventStreamsRestControllerTest {
	private static final String COLLECTION = "name1";
	@MockBean
	private EventStreamService eventStreamService;

	@Autowired
	private MockMvc mockMvc;

	@Nested
	class GetEventStreamList {
		private List<EventStreamTO> eventStreams;

		@BeforeEach
		void initEventStreams() throws URISyntaxException {
			Model shape1 = readModelFromFile("shape-name1.ttl");
			Model shape2 = readModelFromFile("shape-name2.ttl");
			FragmentationConfig fragmentationConfig = new FragmentationConfig();
			fragmentationConfig.setName("ExampleFragmentation");
			fragmentationConfig.setConfig(Map.of("property", "ldes:propertyPath"));
			ViewSpecification singleView = new ViewSpecification(
					new ViewName("name2", "view1"),
					List.of(),
					List.of(fragmentationConfig), 100);
			List<ViewSpecification> views = List.of(
					new ViewSpecification(
							new ViewName(COLLECTION, "view2"),
							List.of(),
							List.of(fragmentationConfig), 100),
					new ViewSpecification(
							new ViewName(COLLECTION, "view3"),
							List.of(),
							List.of(fragmentationConfig), 100));

			eventStreams = List.of(
					new EventStreamTO(COLLECTION, "http://purl.org/dc/terms/created",
							"http://purl.org/dc/terms/isVersionOf", false,
							views, shape1),
					new EventStreamTO("name2", "http://purl.org/dc/terms/created",
							"http://purl.org/dc/terms/isVersionOf", false,
							List.of(singleView),
							shape2));
		}

		@Test
		void when_StreamsPresent_then_StreamsAreReturned() throws Exception {
			Model expectedEventStreamsModel = readModelFromFile("multiple-ldes.ttl");

			when(eventStreamService.retrieveAllEventStreams()).thenReturn(eventStreams);

			mockMvc.perform(get("/admin/api/v1/eventstreams").accept(contentTypeNQuads))
					.andExpect(status().isOk())
					.andExpect(content().contentType(contentTypeNQuads))
					.andExpect(IsIsomorphic.with(expectedEventStreamsModel));

			verify(eventStreamService).retrieveAllEventStreams();
		}
	}

	@Nested
	class GetSingleEventStream {
		@Test
		void when_StreamPresent_Then_StreamIsReturned() throws Exception {
			Model model = readModelFromFile("ldes-1-with-dcat.ttl");
			Model shape = readModelFromFile("example-shape.ttl");
			EventStreamTO eventStream = new EventStreamTO("name1", "http://purl.org/dc/terms/created",
					"http://purl.org/dc/terms/isVersionOf", false,
					List.of(), shape);

			when(eventStreamService.retrieveEventStream(COLLECTION)).thenReturn(eventStream);

			mockMvc.perform(get("/admin/api/v1/eventstreams/" + COLLECTION).accept(contentTypeNQuads))
					.andExpect(status().isOk())
					.andExpect(content().contentType(contentTypeNQuads))
					.andExpect(IsIsomorphic.with(model));

			verify(eventStreamService).retrieveEventStream(COLLECTION);
		}

		@Test
		void when_StreamNotPresent_Then_Returned404() throws Exception {
			when(eventStreamService.retrieveEventStream(COLLECTION)).thenThrow(MissingResourceException.class);

			mockMvc.perform(get("/admin/api/v1/eventstreams/" + COLLECTION).accept(contentTypeTurtle))
					.andExpect(status().isNotFound())
					.andExpect(content().contentType(MediaType.TEXT_PLAIN));

			verify(eventStreamService).retrieveEventStream(COLLECTION);
		}
	}

	@Nested
	class CreateEventStream {
		@Test
		void when_eventStreamModelIsPut_then_eventStreamIsSaved_and_status200IsExpected() throws Exception {
			final Model expectedModel = readModelFromFile("ldes-1-with-dcat.ttl");
			final Model shape = readModelFromFile("example-shape.ttl");

			EventStreamTO eventStreamTO = new EventStreamTO(
					"name1",
					"http://purl.org/dc/terms/created",
					"http://purl.org/dc/terms/isVersionOf",
					false,
					List.of(), shape);

			when(eventStreamService.createEventStream(any(EventStreamTO.class))).thenReturn(eventStreamTO);

			mockMvc.perform(post("/admin/api/v1/eventstreams")
							.accept(contentTypeNQuads)
							.content(readDataFromFile("ldes-1.ttl"))
							.contentType(contentTypeTurtle))
					.andExpect(status().isCreated())
					.andExpect(content().contentType(contentTypeNQuads))
					.andExpect(IsIsomorphic.with(expectedModel));

			verify(eventStreamService).createEventStream(any(EventStreamTO.class));
		}

		@Test
		void when_ModelWithoutType_Then_ReturnedBadRequest() throws Exception {
			mockMvc.perform(post("/admin/api/v1/eventstreams")
							.content(readDataFromFile("ldes-without-type.ttl"))
							.contentType(contentTypeTurtle))
					.andExpect(status().isBadRequest())
					.andExpect(content().contentType(contentTypeTurtle));

			verifyNoInteractions(eventStreamService);
		}

		@Test
		void when_MalformedModelInRequestBody_Then_ReturnedBadRequest() throws Exception {
			mockMvc.perform(post("/admin/api/v1/eventstreams")
							.content(readDataFromFile("malformed-ldes.ttl"))
							.contentType(contentTypeTurtle))
					.andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.TEXT_PLAIN));

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
			doThrow(MissingResourceException.class).when(eventStreamService).deleteEventStream(COLLECTION);

			mockMvc.perform(delete("/admin/api/v1/eventstreams/name1"))
					.andExpect(status().isNotFound())
					.andExpect(content().contentType(MediaType.TEXT_PLAIN));

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
