package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.config.EventStreamWebConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

	@Test
	void when_GetRequestOnCollectionName_EventStreamIsReturned() throws Exception {
		when(eventStreamFetcher.fetchEventStream()).thenReturn(
				new EventStream("collection", "timestampPath", "versionOf", "shape", List.of("viewOne", "viewTwo")));
		ResultActions resultActions = mockMvc.perform(get("/{viewName}",
				ldesConfig.getCollectionName())
				.accept("application/n-quads")).andDo(print())
				.andExpect(status().isOk());
		Model actualModel = RdfModelConverter.fromString(resultActions.andReturn().getResponse().getContentAsString(),
				Lang.NQUADS);
		Model expectedModel = getExpectedModel();
		assertTrue(expectedModel.isIsomorphicWith(actualModel));
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

	private Model getExpectedModel() {
		return RdfModelConverter.fromString(
				"""
						<http://localhost:8080/collection> <https://w3id.org/tree#view> <http://localhost:8080/viewTwo> .
						<http://localhost:8080/collection> <https://w3id.org/tree#view> <http://localhost:8080/viewOne> .
						<http://localhost:8080/collection> <https://w3id.org/ldes#timestampPath> <timestampPath> .
						<http://localhost:8080/collection> <https://w3id.org/ldes#versionOf> <versionOf> .
						<http://localhost:8080/collection> <https://w3id.org/tree#shape> <shape> .
						<http://localhost:8080/collection> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/ldes#EventStream> .""",
				Lang.NQUADS);
	}

	@TestConfiguration
	public static class EventStreamControllerTestConfiguration {

		@Bean
		public EventStreamConverter ldesFragmentConverter(final LdesConfig ldesConfig) {
			PrefixAdder prefixAdder = new PrefixAdderImpl();
			return new EventStreamConverterImpl(prefixAdder, ldesConfig);
		}
	}

}