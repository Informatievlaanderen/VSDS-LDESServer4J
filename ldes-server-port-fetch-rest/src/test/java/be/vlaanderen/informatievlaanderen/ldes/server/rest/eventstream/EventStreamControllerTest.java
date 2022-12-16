package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_EVENT_STREAM_URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
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

import com.github.jsonldjava.shaded.com.google.common.net.HttpHeaders;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.config.EventStreamWebConfig;

@WebMvcTest
@ActiveProfiles("test")
@Import(EventStreamControllerTest.EventStreamControllerTestConfiguration.class)
@ContextConfiguration(classes = { EventStreamController.class,
		LdesConfig.class, EventStreamWebConfig.class })
class EventStreamControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EventStreamFetcher eventStreamFetcher;

	@Autowired
	LdesConfig ldesConfig;

	@ParameterizedTest(name = "Correct getting of an EventStream from the REST Service with mediatype{0}")
	@ArgumentsSource(MediaTypeRdfFormatsArgumentsProvider.class)
	void when_GetRequestOnCollectionName_EventStreamIsReturned(String mediaType, Lang lang) throws Exception {
		when(eventStreamFetcher.fetchEventStream()).thenReturn(
				new EventStream("collection", "timestampPath", "versionOf", "shape", List.of("viewOne", "viewTwo")));
		ResultActions resultActions = mockMvc.perform(get("/{viewName}",
				ldesConfig.getCollectionName())
				.accept(mediaType))
				.andDo(print())
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();

		String etagHeaderValue = result.getResponse().getHeader(HttpHeaders.ETAG);
		String expectedEtagHeaderValue = "0c9111a73bc6a46b00e47c029c2f0e2b340f744d87fce040591d2345dc1d0cb0";
		assertNotNull(etagHeaderValue);
		assertEquals(expectedEtagHeaderValue, etagHeaderValue);

		Model actualModel = RdfModelConverter.fromString(result.getResponse().getContentAsString(),
				lang);
		assertEquals(LDES_EVENT_STREAM_URI, getObjectURI(actualModel,
				RdfConstants.RDF_SYNTAX_TYPE));
	}

	private String getObjectURI(Model model, Property property) {
		return model
				.listStatements(null, property, (Resource) null)
				.nextOptional()
				.map(Statement::getObject)
				.map(RDFNode::asResource)
				.map(Resource::getURI)
				.map(Objects::toString)
				.orElse(null);
	}

	@Test
	@DisplayName("Requesting with Unsupported MediaType returns 406")
	void when_GETRequestIsPerformedWithUnsupportedMediaType_ResponseIs406HttpMediaTypeNotAcceptableException()
			throws Exception {
		when(eventStreamFetcher.fetchEventStream()).thenReturn(
				new EventStream("collection", "timestampPath", "versionOf", "shape", List.of("viewOne", "viewTwo")));

		mockMvc.perform(get("/ldes-fragment").accept("application/json")).andDo(print())
				.andExpect(status().is4xxClientError());
	}

	static class MediaTypeRdfFormatsArgumentsProvider implements
			ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("application/n-quads", Lang.NQUADS),
					Arguments.of("application/turtle", Lang.TURTLE),
					Arguments.of("*/*", Lang.TURTLE));
		}
	}

	@TestConfiguration
	public static class EventStreamControllerTestConfiguration {

		@Bean
		public EventStreamConverter eventStreamConverter(final LdesConfig ldesConfig) {
			PrefixAdder prefixAdder = new PrefixAdderImpl();
			return new EventStreamConverterImpl(prefixAdder, ldesConfig);
		}
	}
}