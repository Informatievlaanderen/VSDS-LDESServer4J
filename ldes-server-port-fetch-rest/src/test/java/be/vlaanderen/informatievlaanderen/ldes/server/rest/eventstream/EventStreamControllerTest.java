package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.EtagCachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.config.EventStreamWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.exceptionhandling.RestResponseEntityExceptionHandler;
import org.apache.http.HttpHeaders;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@Import(EventStreamControllerTest.EventStreamControllerTestConfiguration.class)
@ContextConfiguration(classes = { EventStreamController.class,
		AppConfig.class, RestConfig.class, EventStreamWebConfig.class, RestResponseEntityExceptionHandler.class,
		ViewSpecificationConverter.class, EventStreamResponseConverter.class })
class EventStreamControllerTest {
	private static final String COLLECTION = "mobility-hindrances";
	private static final Integer CONFIGURED_MAX_AGE_IMMUTABLE = 360;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	AppConfig appConfig;
	@Autowired
	RestConfig restConfig;
	@MockBean
	private EventStreamService eventStreamService;
	@MockBean
	private ViewService viewService;
	@MockBean
	private ShaclShapeService shaclShapeService;
	private EventStreamResponse eventStream;
	private ShaclShape shaclShape;
	private String hostname;

	@BeforeEach
	void setUp() {
		hostname = appConfig.getHostName();
		eventStream = new EventStreamResponse(COLLECTION, "http://www.w3.org/ns/prov#generatedAtTime",
				"http://purl.org/dc/terms/isVersionOf", "memberType", List.of(), ModelFactory.createDefaultModel());

		Model shacl = createDefaultModel().add(createResource(appConfig.getHostName() + "/" + COLLECTION),
				createProperty(NODE_SHAPE_TYPE),
				createResource("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape"));

		shaclShape = new ShaclShape(COLLECTION, shacl);

		when(eventStreamService.retrieveEventStream(COLLECTION)).thenReturn(eventStream);
		when(shaclShapeService.retrieveShaclShape(COLLECTION)).thenReturn(shaclShape);
		when(viewService.getViewByViewName(new ViewName(COLLECTION, "by-page"))).thenReturn(
				new ViewSpecification(new ViewName(COLLECTION, "by-page"), List.of(), List.of()));
	}

	@ParameterizedTest(name = "Correct getting of an EventStream from the REST Service with mediatype{0}")
	@ArgumentsSource(MediaTypeRdfFormatsArgumentsProvider.class)
	void when_GetRequestOnCollectionName_EventStreamIsReturned(String mediaType, Lang lang) throws Exception {
		ResultActions resultActions = mockMvc.perform(get("/{viewName}",
				COLLECTION)
				.accept(mediaType))
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();

		String etagHeaderValue = result.getResponse().getHeader(HttpHeaders.ETAG).replace("\"", "");
		String expectedEtagHeaderValue = "d8cd93fb6df91f6d19a6a87c3e645ebe32982a36cee85a75aa084a8ed90f789b";

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
		mockMvc.perform(get("/{collection}", COLLECTION).accept("application/json")).andDo(print())
				.andExpect(status().isUnsupportedMediaType());
	}

	static class MediaTypeRdfFormatsArgumentsProvider implements
			ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("application/n-quads", Lang.NQUADS),
					Arguments.of("application/turtle", Lang.TURTLE),
					Arguments.of("*/*", Lang.TURTLE),
					Arguments.of("", Lang.TURTLE),
					Arguments.of("text/html", Lang.TURTLE));
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
