package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.EventStreamConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.EventStreamConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.EventStreamFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.EtagCachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.config.EventStreamWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.exceptionhandling.RestResponseEntityExceptionHandler;
import org.apache.http.HttpHeaders;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
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

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_EVENT_STREAM_URI;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@Import(EventStreamControllerTest.EventStreamControllerTestConfiguration.class)
@ContextConfiguration(classes = { EventStreamController.class,
		AppConfig.class, RestConfig.class, EventStreamWebConfig.class, RestResponseEntityExceptionHandler.class })
class EventStreamControllerTest {

	private static final Integer CONFIGURED_MAX_AGE_IMMUTABLE = 360;

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private EventStreamFactory eventStreamFactory;
	@Autowired
	AppConfig appConfig;
	@Autowired
	RestConfig restConfig;

	@ParameterizedTest(name = "Correct getting of an EventStream from the REST Service with mediatype{0}")
	@ArgumentsSource(MediaTypeRdfFormatsArgumentsProvider.class)
	void when_GetRequestOnCollectionName_EventStreamIsReturned(String mediaType, Lang lang) throws Exception {
		LdesConfig ldesConfig = appConfig.getLdesConfig("mobility-hindrances");
		when(eventStreamFactory.getEventStream(any())).thenReturn(
				new EventStream(ldesConfig.getCollectionName(), ldesConfig.getTimestampPath(),
						ldesConfig.getVersionOfPath(), ldesConfig.validation().getShape(),
						List.of(createView("viewOne"), createView("viewTwo"))));
		ResultActions resultActions = mockMvc.perform(get("/{viewName}",
				ldesConfig.getCollectionName())
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
				.listStatements(createResource("http://localhost:8080/mobility-hindrances"), property, (Resource) null)
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
		LdesConfig ldesConfig = appConfig.getLdesConfig("mobility-hindrances");
		when(eventStreamFactory.getEventStream(ldesConfig)).thenReturn(
				new EventStream("mobility-hindrances", "timestampPath", "versionOf", "shape",
						List.of(createView("viewOne"), createView("viewTwo"))));

		mockMvc.perform(get("/ldes-fragment").accept("application/json")).andDo(print())
				.andExpect(status().is4xxClientError());
	}

	private TreeNode createView(String viewName) {
		LdesConfig ldesConfig = appConfig.getLdesConfig("mobility-hindrances");

		return new TreeNode(ldesConfig.getBaseUrl() + "/" + viewName, false,
				false, true, List.of(), List.of(), ldesConfig.getCollectionName());
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
		public EventStreamConverter eventStreamConverter(final AppConfig appConfig) {
			PrefixAdder prefixAdder = new PrefixAdderImpl();
			return new EventStreamConverterImpl(prefixAdder, appConfig);
		}

		@Bean
		public CachingStrategy cachingStrategy(final AppConfig appConfig) {
			return new EtagCachingStrategy(appConfig);
		}
	}
}
