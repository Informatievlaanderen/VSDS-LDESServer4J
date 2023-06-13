package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.FragmentationConfigExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.RetentionModelExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.EtagCachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.converters.EventStreamResponseHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.exceptionhandling.RestResponseEntityExceptionHandler;
import org.apache.http.HttpHeaders;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_EVENT_STREAM_URI;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.NODE_SHAPE_TYPE;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@Import(EventStreamControllerTest.EventStreamControllerTestConfiguration.class)
@ContextConfiguration(classes = { EventStreamController.class, AppConfig.class, RestConfig.class,
		RestResponseEntityExceptionHandler.class, EventStreamResponseConverterImpl.class,
		ViewSpecificationConverter.class, PrefixAdderImpl.class, EventStreamResponseHttpConverter.class,
		RetentionModelExtractor.class, ModelConverter.class, FragmentationConfigExtractor.class
})
class EventStreamControllerTest {
	private static final String COLLECTION = "mobility-hindrances";
	private static final Integer CONFIGURED_MAX_AGE_IMMUTABLE = 360;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	AppConfig appConfig;
	@MockBean
	private EventStreamService eventStreamService;
	@MockBean
	private ViewService viewService;
	@MockBean
	private ShaclShapeService shaclShapeService;
	private String hostname;

	@BeforeEach
	void setUp() {
		hostname = appConfig.getHostName();
		EventStreamResponse eventStream = new EventStreamResponse(COLLECTION,
				"http://www.w3.org/ns/prov#generatedAtTime",
				"http://purl.org/dc/terms/isVersionOf", "memberType", List.of(),
				ModelFactory.createDefaultModel());

		Model shacl = createDefaultModel().add(createResource(appConfig.getHostName() + "/" + COLLECTION),
				createProperty(NODE_SHAPE_TYPE),
				createResource("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape"));

		ShaclShape shaclShape = new ShaclShape(COLLECTION, shacl);

		when(eventStreamService.retrieveEventStream(COLLECTION)).thenReturn(eventStream);
		when(shaclShapeService.retrieveShaclShape(COLLECTION)).thenReturn(shaclShape);
		when(viewService.getViewByViewName(new ViewName(COLLECTION, "by-page"))).thenReturn(
				new ViewSpecification(new ViewName(COLLECTION, "by-page"), List.of(), List.of()));
	}

	@ParameterizedTest(name = "Correct getting of an EventStream from the REST Service with mediatype{0}")
	@ArgumentsSource(MediaTypeRdfFormatsArgumentsProvider.class)
	void when_GetRequestOnCollectionName_EventStreamIsReturned(String mediaType, Lang lang,
			String expectedEtagHeaderValue) throws Exception {
		ResultActions resultActions = mockMvc.perform(get("/{viewName}", COLLECTION)
				.accept(mediaType))
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();

		String etagHeaderValue = result.getResponse().getHeader(HttpHeaders.ETAG).replace("\"", "");

		assertNotNull(etagHeaderValue);
		assertEquals(expectedEtagHeaderValue, etagHeaderValue);

		Integer maxAge = extractMaxAge(result);
		assertNotNull(maxAge);
		assertEquals(CONFIGURED_MAX_AGE_IMMUTABLE, maxAge);

		Model actualModel = RdfModelConverter.fromString(result.getResponse().getContentAsString(), lang);
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
		String header = result.getResponse().getHeader(HttpHeaders.CACHE_CONTROL);
		Matcher matcher = Pattern.compile("(.*,)?(max-age=([0-9]+))(,.*)?").matcher(header);

		if (matcher.matches()) {
			return Integer.valueOf(matcher.group(3));
		}

		return null;
	}

	@Test
	@DisplayName("Requesting with Unsupported MediaType returns 406")
	@Disabled("to be enabled once AppConfig:getLdesConfig returns exception again")
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
					Arguments.of("application/turtle", Lang.TURTLE,
							"f922774bd3fe66a59686e17f1bc1e000f591670d7ee70c0c3e1d66377ca08610"),
					Arguments.of("*/*", Lang.TURTLE,
							"adb0e3a84b4ef0dd5356de5961f62a58b1f8a1541dffc62dc7c62644aaed7357"),
					Arguments.of("", Lang.TURTLE,
							"6e14b6fc44f9de48d1f07dd401c81ac5d0116fd26035627eeb2346dda94f60c2"),
					Arguments.of("text/html", Lang.TURTLE,
							"251bb000c5883ec25ff35cb340b9fae08b90ed94d4de89c585df4bf421a501f0"));
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
					.andExpect(result -> {
						String contentAsString = result.getResponse().getContentAsString();
						Model actualModel = RdfModelConverter.fromString(contentAsString, Lang.TURTLE);
						actualModel.isIsomorphicWith(model);
					});

			verify(eventStreamService).getComposedDcat();
		}

		@Test
		void should_ReturnValidationReport_when_Invalid() throws Exception {
			doThrow(new LdesShaclValidationException("validation-report", null)).when(eventStreamService)
					.getComposedDcat();

			mockMvc.perform(get("/")
					.accept(MediaType.ALL))
					.andExpect(status().isInternalServerError());

			verify(eventStreamService).getComposedDcat();
		}
	}

	@TestConfiguration
	public static class EventStreamControllerTestConfiguration {
		@Bean
		public CachingStrategy cachingStrategy(final AppConfig appConfig) {
			return new EtagCachingStrategy(appConfig);
		}
	}
}
