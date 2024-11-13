package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.ValidatorsConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.IsIsomorphic;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamListHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.versioning.AdminVersionHeaderControllerAdvice;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.HttpModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.encodig.CharsetEncodingConfig;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.jena.riot.WebContent.contentTypeNQuads;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({"test", "rest"})
@ContextConfiguration(classes = {AdminEventStreamsRestController.class, HttpModelConverter.class,
		EventStreamListHttpConverter.class, EventStreamHttpConverter.class,
		EventStreamWriter.class, EventStreamReader.class,
		ViewSpecificationConverter.class, PrefixAdderImpl.class, ValidatorsConfig.class,
		AdminRestResponseEntityExceptionHandler.class, RetentionModelExtractor.class, CharsetEncodingConfig.class,
		FragmentationConfigExtractor.class, PrefixConstructor.class, RdfModelConverter.class, AdminVersionHeaderControllerAdvice.class})
@Import(BuildProperties.class)
class AdminEventStreamsRestControllerTest {
	private static final String COLLECTION = "name1";
	public static final String TIMESTAMP_PATH = "http://purl.org/dc/terms/created";
	public static final String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
	@MockBean
	private EventStreamService eventStreamService;
	@SpyBean
	private AdminVersionHeaderControllerAdvice adminVersionHeaderControllerAdvice;

	@Autowired
	private MockMvc mockMvc;

	@Nested
	class GetEventStreamList {
		private List<EventStreamTO> eventStreams;

		@BeforeEach
		void initEventStreams() throws URISyntaxException {
			Model shape1 = readModelFromFile("shacl/shape-name1.ttl");
			Model shape2 = readModelFromFile("shacl/shape-name2.ttl");
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
					new EventStreamTO.Builder()
							.withCollection(COLLECTION)
							.withTimestampPath(TIMESTAMP_PATH)
							.withVersionOfPath(VERSION_OF_PATH)
							.withViews(views)
							.withShacl(shape1)
							.build(),
					new EventStreamTO.Builder()
							.withCollection("name2")
							.withTimestampPath(TIMESTAMP_PATH)
							.withVersionOfPath(VERSION_OF_PATH)
							.withViews(List.of(singleView))
							.withShacl(shape2)
							.build());
		}

		@Test
		void when_StreamsPresent_then_StreamsAreReturned() throws Exception {
			Model expectedEventStreamsModel = readModelFromFile("eventstream/streams/multiple-ldes.ttl");

			when(eventStreamService.retrieveAllEventStreams()).thenReturn(eventStreams);

			mockMvc.perform(get("/admin/api/v1/eventstreams").accept(contentTypeNQuads))
					.andExpect(status().isOk())
					.andExpect(content().encoding(StandardCharsets.UTF_8))
					.andExpect(content().contentTypeCompatibleWith(contentTypeNQuads))
					.andExpect(IsIsomorphic.with(expectedEventStreamsModel));

			verify(eventStreamService).retrieveAllEventStreams();
			verify(adminVersionHeaderControllerAdvice).addVersionHeader(any());
		}
	}

	@Nested
	class GetSingleEventStream {
		@Test
		void when_StreamPresent_Then_StreamIsReturned() throws Exception {
			Model model = readModelFromFile("eventstream/streams-with-dcat/ldes-with-dcat.ttl");
			Model shape = readModelFromFile("shacl/server-shape.ttl");
			EventStreamTO eventStream = new EventStreamTO.Builder()
					.withCollection("name1")
					.withTimestampPath(TIMESTAMP_PATH)
					.withVersionOfPath(VERSION_OF_PATH)
					.withShacl(shape)
					.build();

			when(eventStreamService.retrieveEventStream(COLLECTION)).thenReturn(eventStream);

			mockMvc.perform(get("/admin/api/v1/eventstreams/" + COLLECTION).accept(contentTypeNQuads))
					.andExpect(status().isOk())
					.andExpect(content().encoding(StandardCharsets.UTF_8))
					.andExpect(content().contentTypeCompatibleWith(contentTypeNQuads))
					.andExpect(IsIsomorphic.with(model));

			verify(eventStreamService).retrieveEventStream(COLLECTION);
			verify(adminVersionHeaderControllerAdvice).addVersionHeader(any());
		}

		@Test
		void when_StreamNotPresent_Then_Returned404() throws Exception {
			when(eventStreamService.retrieveEventStream(COLLECTION)).thenThrow(MissingResourceException.class);

			mockMvc.perform(get("/admin/api/v1/eventstreams/" + COLLECTION).accept(contentTypeTurtle))
					.andExpect(status().isNotFound())
					.andExpect(content().encoding(StandardCharsets.UTF_8))
					.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));

			verify(eventStreamService).retrieveEventStream(COLLECTION);
		}
	}

	@Nested
	class CreateEventStream {
		@Test
		void when_eventStreamModelIsPut_then_eventStreamIsSaved_and_status200IsExpected() throws Exception {
			final Model expectedModel = readModelFromFile("eventstream/streams-with-dcat/ldes-with-dcat.ttl");
			final Model shape = readModelFromFile("shacl/server-shape.ttl");

			EventStreamTO eventStreamTO = new EventStreamTO.Builder()
					.withCollection("name1")
					.withTimestampPath(TIMESTAMP_PATH)
					.withVersionOfPath(VERSION_OF_PATH)
					.withShacl(shape)
					.build();

			when(eventStreamService.createEventStream(any(EventStreamTO.class))).thenReturn(eventStreamTO);

			mockMvc.perform(post("/admin/api/v1/eventstreams")
							.accept(contentTypeNQuads)
							.content(readDataFromFile("eventstream/streams/ldes.ttl"))
							.contentType(contentTypeTurtle))
					.andExpect(status().isCreated())
					.andExpect(content().encoding(StandardCharsets.UTF_8))
					.andExpect(content().contentTypeCompatibleWith(contentTypeNQuads))
					.andExpect(IsIsomorphic.with(expectedModel));

			verify(eventStreamService).createEventStream(any(EventStreamTO.class));
		}

		@Test
		void when_eventStreamThatCreateVersionsModelIsPut_then_eventStreamIsSaved_and_status200IsExpected() throws Exception {
			final Model expectedModel = readModelFromFile("eventstream/streams-with-dcat/ldes-create-versions.ttl");
			final Model shape = readModelFromFile("shacl/server-shape.ttl");

			EventStreamTO eventStreamTO = new EventStreamTO.Builder()
					.withCollection("name1")
					.withTimestampPath(TIMESTAMP_PATH)
					.withVersionOfPath(VERSION_OF_PATH)
					.withVersionDelimiter("/")
					.withShacl(shape)
					.build();

			when(eventStreamService.createEventStream(any(EventStreamTO.class))).thenReturn(eventStreamTO);

			mockMvc.perform(post("/admin/api/v1/eventstreams")
							.accept(contentTypeNQuads)
							.content(readDataFromFile("eventstream/streams/ldes-create-versions.ttl"))
							.contentType(contentTypeTurtle))
					.andExpect(status().isCreated())
					.andExpect(content().encoding(StandardCharsets.UTF_8))
					.andExpect(content().contentTypeCompatibleWith(contentTypeNQuads))
					.andExpect(IsIsomorphic.with(expectedModel));

			verify(eventStreamService).createEventStream(assertArg(actual -> assertThat(actual.getVersionDelimiter()).isEqualTo("/")));
		}

		@Test
		void when_ModelWithoutType_Then_ReturnedBadRequest() throws Exception {
			mockMvc.perform(post("/admin/api/v1/eventstreams")
							.content(readDataFromFile("eventstream/streams/ldes-without-type.ttl"))
							.contentType(contentTypeTurtle))
					.andExpect(status().isBadRequest())
					.andExpect(content().encoding(StandardCharsets.UTF_8))
					.andExpect(content().contentTypeCompatibleWith(contentTypeTurtle));

			verifyNoInteractions(eventStreamService);
		}

		@Test
		void when_MalformedModelInRequestBody_Then_ReturnedBadRequest() throws Exception {
			mockMvc.perform(post("/admin/api/v1/eventstreams")
							.content(readDataFromFile("eventstream/streams/malformed-ldes.ttl"))
							.contentType(contentTypeTurtle))
					.andExpect(status().isBadRequest())
					.andExpect(content().encoding(StandardCharsets.UTF_8))
					.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
			verifyNoInteractions(eventStreamService);
		}

		@Test
		void given_EventStreamWithVersionDelimiter_when_POST_then_ReturnCreated() throws Exception {
			final String content = readDataFromFile("eventstream/streams/ldes-with-version-delimiter.ttl").replace("#VERSION_DELIMITER#", "&version=");

			mockMvc.perform(post("/admin/api/v1/eventstreams")
							.content(content)
							.contentType(contentTypeTurtle))
					.andExpect(status().isCreated())
					.andExpect(content().encoding(StandardCharsets.UTF_8))
					.andExpect(content().contentTypeCompatibleWith(contentTypeTurtle));
			verify(eventStreamService).createEventStream(assertArg(actual -> assertThat(actual.getVersionDelimiter()).isEqualTo("&version=")));
		}

		@Test
		void given_EventStreamWithVersionDelimiter_when_POST_then_ReturnBadRequest() throws Exception {
			final String content = readDataFromFile("eventstream/streams/ldes-with-version-delimiter.ttl").replace("#VERSION_DELIMITER#", "");

			mockMvc.perform(post("/admin/api/v1/eventstreams").content(content).contentType(contentTypeTurtle))
					.andExpect(status().isBadRequest());
			verifyNoInteractions(eventStreamService);
		}

		@ParameterizedTest
		@CsvSource({
				"'true', 'false'",
				"'ldes:createVersions true ;', ''"
		})
		void given_EventStreamWithVersionDelimiterAndCreateVersionsFalse_when_POST_then_ReturnBadRequest(String target, String replacement) throws Exception {
			final String content = readDataFromFile("eventstream/streams/ldes-with-version-delimiter.ttl").replace("#VERSION_DELIMITER#", "/").replace(target, replacement);

			mockMvc.perform(post("/admin/api/v1/eventstreams").content(content).contentType(contentTypeTurtle))
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
			doThrow(MissingResourceException.class).when(eventStreamService).deleteEventStream(COLLECTION);

			mockMvc.perform(delete("/admin/api/v1/eventstreams/name1"))
					.andExpect(status().isNotFound())
					.andExpect(content().encoding(StandardCharsets.UTF_8))
					.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
			verify(eventStreamService).deleteEventStream(COLLECTION);
		}
	}

	@Nested
	class CloseEventStream {
		@Test
		void when_collectionExists_and_triesToClose_then_expectStatus200() throws Exception {
			mockMvc.perform(post("/admin/api/v1/eventstreams/name1/close"))
					.andExpect(status().isOk());

			verify(eventStreamService).closeEventStream(COLLECTION);
		}

		@Test
		void when_collectionDoesNotExist_then_expectStatus404() throws Exception {
			doThrow(MissingResourceException.class).when(eventStreamService).closeEventStream(COLLECTION);

			mockMvc.perform(post("/admin/api/v1/eventstreams/name1/close"))
					.andExpect(status().isNotFound())
					.andExpect(content().encoding(StandardCharsets.UTF_8))
					.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));

			verify(eventStreamService).closeEventStream(COLLECTION);
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
