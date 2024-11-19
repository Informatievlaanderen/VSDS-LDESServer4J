package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.HttpModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.EtagCachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.converters.EventStreamResponseHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.exceptionhandling.RestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.versioning.VersionHeaderFilterConfig;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_EVENT_STREAM_URI;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.HOST_NAME_KEY;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({"test", "rest"})
@Import({EventStreamControllerTest.EventStreamControllerTestConfiguration.class})
@ContextConfiguration(classes = {EventStreamController.class, RestConfig.class,
		RestResponseEntityExceptionHandler.class, EventStreamWriter.class, EventStreamReader.class, KafkaSourceReader.class,
		ViewSpecificationConverter.class, PrefixAdderImpl.class, EventStreamResponseHttpConverter.class,
		RetentionModelExtractor.class, HttpModelConverter.class, FragmentationConfigExtractor.class,
		PrefixConstructor.class, RdfModelConverter.class, VersionHeaderFilterConfig.class
})
class EventStreamControllerTest {
	private static final String COLLECTION = "mobility-hindrances";
	private static final Integer CONFIGURED_MAX_AGE_MUTABLE = 180;
	private static final String VERSION = "4.0.4-SNAPSHOT";

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private EventStreamServiceSpi eventStreamService;
	private String hostname;

	@BeforeEach
	void setUp() {
		hostname = "http://localhost:8080";

		EventStreamTO eventStream = new EventStreamTO.Builder()
				.withCollection(COLLECTION)
				.withTimestampPath("http://www.w3.org/ns/prov#generatedAtTime")
				.withVersionOfPath("http://purl.org/dc/terms/isVersionOf")
				.withShacl(ModelFactory.createDefaultModel())
				.build();

		when(eventStreamService.retrieveEventStream(COLLECTION)).thenReturn(eventStream);
	}

	@ParameterizedTest(name = "Correct getting of an EventStream from the REST Service with mediatype{0}")
	@ArgumentsSource(MediaTypeRdfFormatsArgumentsProvider.class)
	void when_GetRequestOnCollectionName_EventStreamIsReturned(String mediaType, Lang lang,
	                                                           String expectedEtagHeaderValue) throws Exception {
		ResultActions resultActions = mockMvc.perform(get("/{viewName}", COLLECTION)
						.accept(mediaType))
				.andExpect(header().string("X-App-Version", VERSION))
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();

		String etagHeaderValue = Objects.requireNonNull(result.getResponse().getHeader(HttpHeaders.ETAG)).replace("\"", "");

		assertNotNull(etagHeaderValue);
		assertEquals(expectedEtagHeaderValue, etagHeaderValue);

		Integer maxAge = extractMaxAge(result);
		assertNotNull(maxAge);
		assertEquals(CONFIGURED_MAX_AGE_MUTABLE, maxAge);

		InputStream inputStream = new ByteArrayInputStream(result.getResponse().getContentAsByteArray());
		Model actualModel = RDFParser.source(inputStream).lang(lang).toModel();
		assertEquals(LDES_EVENT_STREAM_URI, getObjectURI(actualModel, RdfConstants.RDF_SYNTAX_TYPE));
	}

	private String getObjectURI(Model model, Property property) {
		return model
				.listStatements(createResource(hostname + "/" + COLLECTION), property, (Resource) null)
				.nextOptional()
				.map(Statement::getObject)
				.map(RDFNode::asResource)
				.map(Resource::getURI)
				.map(Objects::toString)
				.orElse(null);
	}

	private Integer extractMaxAge(MvcResult result) {
		String header = Objects.requireNonNull(result.getResponse().getHeader(HttpHeaders.CACHE_CONTROL));
		Matcher matcher = Pattern.compile("(.*,)?(max-age=([0-9]+))(,.*)?").matcher(header);

		if (matcher.matches()) {
			return Integer.valueOf(matcher.group(3));
		}

		return null;
	}

	@Test
	@DisplayName("Requesting with Unsupported MediaType returns 406")
	void when_GETRequestIsPerformedWithUnsupportedMediaType_ResponseIs406HttpMediaTypeNotAcceptableException()
			throws Exception {
		mockMvc.perform(get("/{collection}", COLLECTION).accept("application/json"))
				.andExpect(status().isUnsupportedMediaType());
	}

	static class MediaTypeRdfFormatsArgumentsProvider implements
			ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("application/n-quads", Lang.NQUADS,
							"33a964ecee072d6e4c97a3522d3b5fc58d752c1d69276a36eddcb640ba90a509"),
					Arguments.of("text/turtle", Lang.TURTLE,
							"e05c986413efc584c7e9534dae550a242f26fd8164d8fff80d035caaa9f05573"),
					Arguments.of("*/*", Lang.TURTLE,
							"adb0e3a84b4ef0dd5356de5961f62a58b1f8a1541dffc62dc7c62644aaed7357"),
					Arguments.of("", Lang.TURTLE,
							"e05c986413efc584c7e9534dae550a242f26fd8164d8fff80d035caaa9f05573"),
					Arguments.of("text/html", Lang.TURTLE,
							"251bb000c5883ec25ff35cb340b9fae08b90ed94d4de89c585df4bf421a501f0"),
					Arguments.of("application/rdf+protobuf", Lang.RDFPROTO,
							"5494c476036bca2a305948898e413308a2377a342064c9a36d6d0ab1dd72f7f2")
			);
		}
	}

	@Nested
	class GetDcat {

		@Test
		void should_ReturnDcat_when_Valid() throws Exception {
			final Model model = RDFParser.source("dcat/valid-server-dcat.ttl").lang(Lang.TURTLE).toModel();

			when(eventStreamService.getComposedDcat()).thenReturn(model);

			mockMvc.perform(get("/")
							.accept(MediaType.ALL))
					.andExpect(status().isOk())
					.andExpect(header().string("X-App-Version", VERSION))
					.andExpect(result -> {
						String contentAsString = result.getResponse().getContentAsString();
						Model actualModel = RDFParser.create().fromString(contentAsString).lang(Lang.TURTLE).toModel();
						actualModel.isIsomorphicWith(model);
					});

			verify(eventStreamService).getComposedDcat();
		}

		@Test
		void should_ReturnValidationReport_when_Invalid() throws Exception {
			doThrow(new ShaclValidationException("validation-report", ModelFactory.createDefaultModel())).when(eventStreamService)
					.getComposedDcat();

			mockMvc.perform(get("/")
							.accept(MediaType.ALL))
					.andExpect(status().isBadRequest());

			verify(eventStreamService).getComposedDcat();
		}
	}

	@TestConfiguration
	public static class EventStreamControllerTestConfiguration {
		@Bean
		public CachingStrategy cachingStrategy(@Value(HOST_NAME_KEY) String hostName) {
			return new EtagCachingStrategy(hostName);
		}

		@Bean
		public BuildProperties buildProperties() {
			final Properties properties = new Properties();
			properties.put("version", VERSION);
			return new BuildProperties(properties);
		}
	}
}
