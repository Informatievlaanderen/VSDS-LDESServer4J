package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentFetchService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.LdesFragmentConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.LdesFragmentConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.WebConfig;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
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
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.PROV_GENERATED_AT_TIME;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.VERSION_OF_URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles("test")
@Import(LdesFragmentControllerTest.LdesFragmentControllerTestConfiguration.class)
@ContextConfiguration(classes = { LdesFragmentController.class, LdesConfig.class, WebConfig.class })
class LdesFragmentControllerTest {
	private static final String LDES_EVENTSTREAM = "https://w3id.org/ldes#EventStream";
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";

	private static final String GENERATED_AT_TIME = "generatedAtTime";
	private static final String FRAGMENT_ID = "http://localhost:8080/mobility-hindrances?" + GENERATED_AT_TIME + "="
			+ FRAGMENTATION_VALUE_1;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private LdesConfig ldesConfig;
	@MockBean
	private FragmentFetchService fragmentFetchService;

	@Test
	@DisplayName("Correct getting of an initial empty LdesFragment")
	void when_GETRequestIsPerformedOnBaseURLAndNoFragmentIsCreatedYet_ResponseContainsAnEmptyLDesFragment()
			throws Exception {
		String fragmentId = "%s/%s?generatedAtTime=%s".formatted(ldesConfig.getHostName(),
				ldesConfig.getCollectionName(), FRAGMENTATION_VALUE_1);
		LdesFragment emptyFragment = new LdesFragment(fragmentId,
				new FragmentInfo(ldesConfig.getCollectionName(), List.of()));
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(ldesConfig.getCollectionName(), List.of());
		when(fragmentFetchService.getInitialFragment(ldesFragmentRequest)).thenReturn(emptyFragment);

		ResultActions resultActions = mockMvc
				.perform(get("/{viewShortName}", ldesConfig.getCollectionName()).accept("application/ld+json"))
				.andDo(print())
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();
		String headerValue = result.getResponse().getHeader("Cache-Control");
		assertEquals("public, max-age=60", headerValue);
		Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString())
				.lang(Lang.JSONLD11)
				.toModel();
		assertEquals(ldesConfig.getShape(), getObjectURI(resultModel, RdfConstants.TREE_SHAPE));
		assertEquals(PROV_GENERATED_AT_TIME, getObjectURI(resultModel, RdfConstants.LDES_TIMESTAMP_PATH));
		assertEquals(VERSION_OF_URI, getObjectURI(resultModel, RdfConstants.LDES_VERSION_OF));
		assertEquals(LDES_EVENTSTREAM, getObjectURI(resultModel, RdfConstants.RDF_SYNTAX_TYPE));
		verify(fragmentFetchService, times(1)).getInitialFragment(ldesFragmentRequest);
		verifyNoMoreInteractions(fragmentFetchService);
	}

	@Test
	@DisplayName("Correct redirecting to first fragment")
	void when_GETRequestIsPerformedOnBaseURLAndFragmentIsAvailable_FragmentIsReturnedAndResponseContainsRedirect()
			throws Exception {
		String fragmentId = "%s/%s?generatedAtTime=%s".formatted(ldesConfig.getHostName(),
				ldesConfig.getCollectionName(), FRAGMENTATION_VALUE_1);
		LdesFragment realFragment = new LdesFragment(fragmentId, new FragmentInfo(ldesConfig.getCollectionName(),
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1))));
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(ldesConfig.getCollectionName(), List.of());
		when(fragmentFetchService.getInitialFragment(ldesFragmentRequest)).thenReturn(realFragment);

		ResultActions resultActions = mockMvc
				.perform(get("/{viewShortName}", ldesConfig.getCollectionName()).accept("application/ld+json"))
				.andDo(print())
				.andExpect(status().is3xxRedirection());

		MvcResult result = resultActions.andReturn();
		String headerValue = result.getResponse().getHeader("Cache-Control");
		assertEquals("public, max-age=60", headerValue);
		Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString())
				.lang(Lang.JSONLD11)
				.toModel();
		assertEquals(ldesConfig.getShape(), getObjectURI(resultModel, RdfConstants.TREE_SHAPE));
		assertEquals(PROV_GENERATED_AT_TIME, getObjectURI(resultModel, RdfConstants.LDES_TIMESTAMP_PATH));
		assertEquals(VERSION_OF_URI, getObjectURI(resultModel, RdfConstants.LDES_VERSION_OF));
		assertEquals(fragmentId, getObjectURI(resultModel, RdfConstants.TREE_VIEW));
		assertEquals(LDES_EVENTSTREAM, getObjectURI(resultModel, RdfConstants.RDF_SYNTAX_TYPE));
		verify(fragmentFetchService, times(1)).getInitialFragment(ldesFragmentRequest);
		verifyNoMoreInteractions(fragmentFetchService);
	}

	@Test
	@DisplayName("Correct returning a complete fragment")
	void when_GETRequestIsPerformedAndFragmentIsAvailable_FragmentIsReturnedAndResponseContainsRedirect()
			throws Exception {
		String fragmentId = "%s/%s?generatedAtTime=%s".formatted(ldesConfig.getHostName(),
				ldesConfig.getCollectionName(), FRAGMENTATION_VALUE_1);
		FragmentInfo fragmentInfo = new FragmentInfo(ldesConfig.getCollectionName(),
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		fragmentInfo.setImmutable(true);
		LdesFragment realFragment = new LdesFragment(fragmentId, fragmentInfo);
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(ldesConfig.getCollectionName(),
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		when(fragmentFetchService.getFragment(ldesFragmentRequest)).thenReturn(realFragment);

		ResultActions resultActions = mockMvc
				.perform(get("/{viewShortName}", ldesConfig.getCollectionName())
						.param("generatedAtTime", FRAGMENTATION_VALUE_1)
						.accept("application/ld+json"))
				.andDo(print())
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();
		String headerValue = result.getResponse().getHeader("Cache-Control");
		assertEquals("public, max-age=604800, immutable", headerValue);
		Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString())
				.lang(Lang.JSONLD11)
				.toModel();
		assertEquals(ldesConfig.getShape(), getObjectURI(resultModel, RdfConstants.TREE_SHAPE));
		assertEquals(PROV_GENERATED_AT_TIME, getObjectURI(resultModel, RdfConstants.LDES_TIMESTAMP_PATH));
		assertEquals(VERSION_OF_URI, getObjectURI(resultModel, RdfConstants.LDES_VERSION_OF));
		assertEquals(fragmentId, getObjectURI(resultModel, RdfConstants.TREE_VIEW));
		assertEquals(LDES_EVENTSTREAM, getObjectURI(resultModel, RdfConstants.RDF_SYNTAX_TYPE));
		verify(fragmentFetchService, times(1)).getFragment(ldesFragmentRequest);
		verifyNoMoreInteractions(fragmentFetchService);
	}

	@ParameterizedTest(name = "Correct getting of an open LdesFragment from the REST Service with mediatype {0}")
	@ArgumentsSource(MediaTypeRdfFormatsArgumentsProvider.class)
	void when_GETRequestIsPerformed_ResponseContainsAnLDesFragment(String mediaType, Lang lang) throws Exception {
		String fragmentId = "%s/%s?generatedAtTime=%s".formatted(ldesConfig.getHostName(),
				ldesConfig.getCollectionName(), FRAGMENTATION_VALUE_1);
		LdesFragment ldesFragment = new LdesFragment(fragmentId, new FragmentInfo(ldesConfig.getCollectionName(),
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1))));

		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(ldesConfig.getCollectionName(),
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		when(fragmentFetchService.getFragment(ldesFragmentRequest)).thenReturn(ldesFragment);

		ResultActions resultActions = mockMvc.perform(get("/{viewShortName}", ldesConfig.getCollectionName())
				.param("generatedAtTime", FRAGMENTATION_VALUE_1).accept(mediaType)).andDo(print())
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();
		Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString()).lang(lang)
				.toModel();
		assertEquals(ldesConfig.getShape(), getObjectURI(resultModel, RdfConstants.TREE_SHAPE));
		assertEquals(PROV_GENERATED_AT_TIME, getObjectURI(resultModel, RdfConstants.LDES_TIMESTAMP_PATH));
		assertEquals(VERSION_OF_URI, getObjectURI(resultModel, RdfConstants.LDES_VERSION_OF));
		assertEquals(FRAGMENT_ID, getObjectURI(resultModel, RdfConstants.TREE_VIEW));
		assertEquals(LDES_EVENTSTREAM, getObjectURI(resultModel, RdfConstants.RDF_SYNTAX_TYPE));
		verify(fragmentFetchService, times(1)).getFragment(ldesFragmentRequest);
	}

	public String getObjectURI(Model model, Property property) {
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
		String fragmentId = "%s/%s?generatedAtTime=%s".formatted(ldesConfig.getHostName(),
				ldesConfig.getCollectionName(), FRAGMENTATION_VALUE_1);
		LdesFragment ldesFragment = new LdesFragment(fragmentId, new FragmentInfo(null, List.of()));

		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(ldesConfig.getCollectionName(),
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		when(fragmentFetchService.getFragment(ldesFragmentRequest)).thenReturn(ldesFragment);

		mockMvc.perform(get("/ldes-fragment").accept("application/json")).andDo(print())
				.andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("Requesting using another collection name returns 404")
	void when_GETRequestIsPerformedOnOtherCollectionName_ResponseIs404() throws Exception {
		mockMvc.perform(get("/abba")
				.param("generatedAtTime", FRAGMENTATION_VALUE_1).accept("application/n-quads")).andDo(print())
				.andExpect(status().isNotFound());
	}

	static class MediaTypeRdfFormatsArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(Arguments.of("application/n-quads", Lang.NQUADS),
					Arguments.of("application/ld+json", Lang.JSONLD11),
					Arguments.of("application/turtle", Lang.TURTLE));
		}
	}

	@TestConfiguration
	public static class LdesFragmentControllerTestConfiguration {

		@Bean
		public LdesFragmentConverter ldesFragmentConverter(final LdesConfig ldesConfig) {
			LdesMemberRepository ldesMemberRepository = mock(LdesMemberRepository.class);
			return new LdesFragmentConverterImpl(ldesMemberRepository, ldesConfig);
		}
	}
}