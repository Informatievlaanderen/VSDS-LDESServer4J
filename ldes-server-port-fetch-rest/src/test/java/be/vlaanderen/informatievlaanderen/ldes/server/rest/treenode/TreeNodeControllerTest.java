package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.*;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.TreeNodeFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.EtagCachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.exceptionhandling.RestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.config.TreeViewWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services.TreeNodeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services.TreeNodeConverterImpl;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.HOST_NAME_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ActiveProfiles({"test", "rest"})
@Import(TreeNodeControllerTest.TreeNodeControllerTestConfiguration.class)
@ContextConfiguration(classes = {TreeNodeController.class,
		RestConfig.class, TreeViewWebConfig.class,
		RestResponseEntityExceptionHandler.class})
class TreeNodeControllerTest {
	private static final String COLLECTION_NAME = "ldes-1";
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private static final String VIEW_NAME = "view";
	private String fullViewName;
	private static final Integer CONFIGURED_MAX_AGE = 180;
	private static final Integer CONFIGURED_MAX_AGE_IMMUTABLE = 360;

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private TreeNodeFetcher treeNodeFetcher;
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		fullViewName = COLLECTION_NAME + "/" + VIEW_NAME;
	}

	@ParameterizedTest(name = "Correct getting of an open LdesFragment from the  REST Service with mediatype{0}")
	@ArgumentsSource(MediaTypeRdfFormatsArgumentsProvider.class)
	void when_GETRequestIsPerformed_ResponseContainsAnLDesFragment(String mediaType, Lang lang, boolean immutable,
																   String expectedHeaderValue, String expectedEtag) throws Exception {
		EventStream eventStream = new EventStream(COLLECTION_NAME, null, null, null);
		eventPublisher.publishEvent(new EventStreamCreatedEvent(eventStream));

		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(ViewName.fromString(fullViewName),
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		final String fragmentId = new LdesFragmentIdentifier(ldesFragmentRequest.viewName(),
				ldesFragmentRequest.fragmentPairs())
				.asString();
		TreeNode treeNode = new TreeNode(fragmentId, immutable, false, List.of(),
				List.of(), COLLECTION_NAME);

		when(treeNodeFetcher.getFragment(ldesFragmentRequest)).thenReturn(treeNode);

		ResultActions resultActions = mockMvc
				.perform(get("/{collectionName}/{viewName}", COLLECTION_NAME, VIEW_NAME)
						.param("generatedAtTime", FRAGMENTATION_VALUE_1)
						.accept(mediaType))
				.andExpect(status().isOk())
				.andExpect(header().string("Cache-Control", expectedHeaderValue))
				.andExpect(header().string("Etag", "\"" + expectedEtag + "\""));

		MvcResult result = resultActions.andReturn();
		Optional<Integer> maxAge = extractMaxAge(result.getResponse().getHeader("Cache-Control"));
		Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString()).lang(lang)
				.toModel();

		assertThat(maxAge).contains(immutable ? CONFIGURED_MAX_AGE_IMMUTABLE : CONFIGURED_MAX_AGE);
		assertThat(getObjectURI(resultModel, RDF_SYNTAX_TYPE)).isEqualTo(TREE_NODE_RESOURCE);
		verify(treeNodeFetcher, times(1)).getFragment(ldesFragmentRequest);
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

	private Optional<Integer> extractMaxAge(String header) {
		Matcher matcher = Pattern.compile("(.*,)?(max-age=([0-9]+))(,.*)?").matcher(header);
		return matcher.matches() ? Optional.of(Integer.valueOf(matcher.group(3))) : Optional.empty();
	}

	@Test
	@DisplayName("Requesting with Unsupported MediaType returns 406")
	void when_GETRequestIsPerformedWithUnsupportedMediaType_ResponseIs406HttpMediaTypeNotAcceptableException()
			throws Exception {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(
				ViewName.fromString(fullViewName), List.of());
		final String fragmentId = new LdesFragmentIdentifier(ldesFragmentRequest.viewName(),
				ldesFragmentRequest.fragmentPairs())
				.asString();
		TreeNode treeNode = new TreeNode(fragmentId, false, false, List.of(),
				List.of(), COLLECTION_NAME);
		when(treeNodeFetcher.getFragment(ldesFragmentRequest)).thenReturn(treeNode);
		mockMvc.perform(get("/{collectionName}/{viewName}", COLLECTION_NAME, VIEW_NAME)
						.accept("application/json"))
				.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	void when_GETRequestButMissingFragmentExceptionIsThrown_NotFoundIsReturned() throws Exception {

		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(
				ViewName.fromString(fullViewName),
				List.of());
		when(treeNodeFetcher.getFragment(ldesFragmentRequest))
				.thenThrow(new MissingResourceException("fragment", "fragmentId"));

		mockMvc.perform(get("/{collectionName}/{viewName}", COLLECTION_NAME, VIEW_NAME)
						.accept("application/n-quads"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Resource of type: fragment with id: fragmentId could not be found."));
	}

	@Test
	@DisplayName("Requesting using another collection name returns 404")
	void when_GETRequestIsPerformedOnOtherCollectionName_ResponseIs404() throws Exception {
		mockMvc.perform(get("/")
						.param("generatedAtTime", FRAGMENTATION_VALUE_1)
						.accept("application/n-quads"))
				.andExpect(status().isNotFound());
	}

	static class MediaTypeRdfFormatsArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("application/n-quads", Lang.NQUADS, true,
							"public,max-age=" + CONFIGURED_MAX_AGE_IMMUTABLE + ",immutable",
							"4dad435c3bea4079e22a38b5320483522442960e435ae47682b8eb7d0d64e034"),
					Arguments.of("application/ld+json", Lang.JSONLD10, true,
							"public,max-age=" + CONFIGURED_MAX_AGE_IMMUTABLE + ",immutable",
							"10235d5bcd85b9450bfcbb423d4a8f0f9da876542c3f9690a24794cef459fbd8"),
					Arguments.of("application/turtle", Lang.TURTLE, false, "public,max-age=" + CONFIGURED_MAX_AGE,
							"92ab436c5dac07ab3b47d727354fa6bf69b5ea1dd8253b87c2badf3341d34b3e"),
					Arguments.of("*/*", Lang.TURTLE, false, "public,max-age=" + CONFIGURED_MAX_AGE,
							"eb83737d75dc70fed31daf4846abb18f2787caa566f1e9af10f2520dc22b9e4f"),
					Arguments.of("", Lang.TURTLE, false, "public,max-age=" + CONFIGURED_MAX_AGE,
							"c6536f80ad110d5d365e84ae1398ff90b9afbc0a7d7bec8738bac9204d63f12f"),
					Arguments.of("text/html", Lang.TURTLE, false, "public,max-age=" + CONFIGURED_MAX_AGE,
							"eab5179ac011c835cb460a0bdc6a28a52491255197a1073d2b963675961e66f2"));
		}
	}

	@TestConfiguration
	public static class TreeNodeControllerTestConfiguration {

		@Bean
		public TreeNodeConverter ldesFragmentConverter(@Value(HOST_NAME_KEY) String hostName) {
			PrefixAdder prefixAdder = new PrefixAdderImpl();
			return new TreeNodeConverterImpl(prefixAdder, hostName);
		}

		@Bean
		public CachingStrategy cachingStrategy(@Value(HOST_NAME_KEY) String hostName) {
			return new EtagCachingStrategy(hostName);
		}
	}
}
