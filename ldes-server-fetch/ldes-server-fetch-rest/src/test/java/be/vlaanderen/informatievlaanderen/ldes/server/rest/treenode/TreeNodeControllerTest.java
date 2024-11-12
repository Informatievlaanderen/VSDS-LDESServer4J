package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.encodig.CharsetEncodingConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.StreamingTreeNodeFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.TreeNodeFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.EtagCachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.RestConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.exceptionhandling.RestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.config.TreeViewWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services.*;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.versioning.VersionHeaderControllerAdvice;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
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
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.HOST_NAME_KEY;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ActiveProfiles({"test", "rest"})
@Import(TreeNodeControllerTest.TreeNodeControllerTestConfiguration.class)
@ContextConfiguration(classes = {TreeNodeController.class,
		RestConfig.class, TreeViewWebConfig.class,
		RestResponseEntityExceptionHandler.class, PrefixConstructor.class,
		RdfModelConverter.class, TreeNodeStreamConverterImpl.class, PrefixAdderImpl.class,
		TreeNodeStatementCreatorImpl.class, CharsetEncodingConfig.class, VersionHeaderControllerAdvice.class,
})
class TreeNodeControllerTest {
	private static final String COLLECTION_NAME = "ldes-1";
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private static final String VIEW_NAME = "view";
	private String fullViewName;
	private static final Integer CONFIGURED_MAX_AGE = 180;
	private static final Integer CONFIGURED_MAX_AGE_IMMUTABLE = 360;
	private static final String VERSION = "4.0.4-SNAPSHOT";

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private TreeNodeFetcher treeNodeFetcher;
	@MockBean
	private StreamingTreeNodeFactory streamingTreeNodeFactory;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private TreeNodeStreamConverter treeNodeStreamConverter;
	@Autowired
	private RestConfig restConfig;
	@Autowired
	private CachingStrategy cachingStrategy;

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
				.asDecodedFragmentId();
		TreeNode treeNode = new TreeNode(fragmentId, immutable, false, List.of(),
				List.of(), COLLECTION_NAME, null);

		when(treeNodeFetcher.getFragment(ldesFragmentRequest)).thenReturn(treeNode);

		var expectedContentType = switch (mediaType) {
			case MediaType.ALL_VALUE, "", "text/html" -> contentTypeTurtle;
			default -> mediaType;
		};

		MvcResult result = mockMvc
				.perform(get("/{collectionName}/{viewName}", COLLECTION_NAME, VIEW_NAME)
						.param("generatedAtTime", FRAGMENTATION_VALUE_1)
						.accept(mediaType))
				.andExpect(status().isOk())
				.andExpect(header().string("Cache-Control", expectedHeaderValue))
				.andExpect(header().string("Etag", "\"" + expectedEtag + "\""))
				.andExpect(header().string("X-App-Version", VERSION))
				.andExpect(content().encoding(StandardCharsets.UTF_8))
				.andExpect(content().contentTypeCompatibleWith(expectedContentType))
				.andReturn();

		Optional<Integer> maxAge = extractMaxAge(result.getResponse().getHeader("Cache-Control"));
		InputStream inputStream = new ByteArrayInputStream(result.getResponse().getContentAsByteArray());
		Model resultModel = RDFParser.source(inputStream).lang(lang).toModel();

		assertThat(maxAge).contains(immutable ? CONFIGURED_MAX_AGE_IMMUTABLE : CONFIGURED_MAX_AGE);
		assertThat(getObjectURIs(resultModel, RDF_SYNTAX_TYPE)).contains(TREE_NODE_RESOURCE);
		verify(treeNodeFetcher, times(1)).getFragment(ldesFragmentRequest);
	}


	@Test
	void given_MemberWithSpecialChars_when_GetRequestIsPerformed_then_CharsAreRightfullyEncoded() throws Exception {
		EventStream eventStream = new EventStream(COLLECTION_NAME, null, null, null);
		eventPublisher.publishEvent(new EventStreamCreatedEvent(eventStream));

		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(ViewName.fromString(fullViewName),
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		final String fragmentId = new LdesFragmentIdentifier(ldesFragmentRequest.viewName(),
				ldesFragmentRequest.fragmentPairs())
				.asDecodedFragmentId();
		final Member member = createMember();
		TreeNode treeNode = new TreeNode(fragmentId, true, false, List.of(),
				List.of(member), COLLECTION_NAME, null);

		when(treeNodeFetcher.getFragment(ldesFragmentRequest)).thenReturn(treeNode);


		String mediaType = "text/turtle";
		mockMvc
				.perform(get("/{collectionName}/{viewName}", COLLECTION_NAME, VIEW_NAME)
						.param("generatedAtTime", FRAGMENTATION_VALUE_1)
						.accept(mediaType))
				.andExpect(status().isOk())
				.andExpect(content().encoding(StandardCharsets.UTF_8))
				.andExpect(header().string("X-App-Version", VERSION))
				.andExpect(content().contentTypeCompatibleWith(mediaType))
				.andExpect(content().string(containsString("ë")))
				.andExpect(content().string(containsString("你好")))
				.andReturn();

		verify(treeNodeFetcher, times(1)).getFragment(ldesFragmentRequest);
	}

	private List<String> getObjectURIs(Model model, Property property) {
		return model
				.listStatements(null, property, (Resource) null)
				.toList()
				.stream()
				.map(Statement::getObject)
				.map(RDFNode::asResource)
				.map(Resource::getURI)
				.map(Objects::toString)
				.toList();
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
				.asDecodedFragmentId();
		TreeNode treeNode = new TreeNode(fragmentId, false, false, List.of(),
				List.of(), COLLECTION_NAME, null);
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
				.thenThrow(new MissingResourceException("fragment", "bucketDescriptor"));

		mockMvc.perform(get("/{collectionName}/{viewName}", COLLECTION_NAME, VIEW_NAME)
						.accept("application/n-quads"))
				.andExpect(status().isNotFound())
				.andExpect(header().string("X-App-Version", VERSION))
				.andExpect(content().string("Resource of type: fragment with id: bucketDescriptor could not be found."));
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
		@SuppressWarnings("java:S1874") // deprecated Lang used for testing backwards compatibility
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("application/n-quads", Lang.NQUADS, true,
							"public,max-age=" + CONFIGURED_MAX_AGE_IMMUTABLE + ",immutable",
							"4dad435c3bea4079e22a38b5320483522442960e435ae47682b8eb7d0d64e034"),
					Arguments.of("application/rdf+protobuf", Lang.RDFPROTO, true,
							"public,max-age=" + CONFIGURED_MAX_AGE_IMMUTABLE + ",immutable",
							"cfc950c3f081507614d7b4f0f9d4ef65fe59a45048f62c9b80486ff4c7346e49"),
					Arguments.of("text/turtle", Lang.TURTLE, false, "public,max-age=" + CONFIGURED_MAX_AGE,
							"c6536f80ad110d5d365e84ae1398ff90b9afbc0a7d7bec8738bac9204d63f12f"),
					Arguments.of("*/*", Lang.TURTLE, false, "public,max-age=" + CONFIGURED_MAX_AGE,
							"eb83737d75dc70fed31daf4846abb18f2787caa566f1e9af10f2520dc22b9e4f"),
					Arguments.of("", Lang.TURTLE, false, "public,max-age=" + CONFIGURED_MAX_AGE,
							"c6536f80ad110d5d365e84ae1398ff90b9afbc0a7d7bec8738bac9204d63f12f"),
					Arguments.of("text/html", Lang.TURTLE, false, "public,max-age=" + CONFIGURED_MAX_AGE,
							"eab5179ac011c835cb460a0bdc6a28a52491255197a1073d2b963675961e66f2")
			);
		}
	}

	@Test
	@DisplayName("Requesting LDES fragment stream")
	void when_GETRequestIsPerformedForStreaming_ResponseContainsAnLDesFragment() {
		EventStream eventStream = new EventStream(COLLECTION_NAME, null, null, null);
		eventPublisher.publishEvent(new EventStreamCreatedEvent(eventStream));

		LdesFragmentIdentifier identifier = new LdesFragmentIdentifier(ViewName.fromString(fullViewName),
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		final String fragmentId = identifier.asDecodedFragmentId();
		TreeNode treeNode = new TreeNode(fragmentId, false, false, List.of(),
				List.of(), COLLECTION_NAME, null);

		when(streamingTreeNodeFactory.getFragmentWithoutMemberData(identifier)).thenReturn(treeNode);
		when(streamingTreeNodeFactory.getMembersOfFragment(identifier))
				.thenReturn(Stream.of(new Member("member1", ModelFactory.createDefaultModel()), new Member("member2", ModelFactory.createDefaultModel())));

		WebTestClient client = WebTestClient.bindToController(new TreeNodeController(restConfig, treeNodeFetcher,
				streamingTreeNodeFactory, treeNodeStreamConverter, cachingStrategy)).build();

		client.get()
				.uri("/{collectionName}/{viewName}?generatedAtTime={fragmentationValue}", COLLECTION_NAME, VIEW_NAME, FRAGMENTATION_VALUE_1)
				.accept(MediaType.TEXT_EVENT_STREAM)
				.exchange()
				.expectStatus()
				.isOk()
				.expectHeader()
				.valueEquals("Cache-Control", "public,max-age=" + CONFIGURED_MAX_AGE)
				.expectHeader()
				.valueEquals("Etag", "\"bf61f90ee94d31484e296ffaa887de432976ce27638cfbd35e40f99a0e799554\"")
				.returnResult(String.class);
	}

	@TestConfiguration
	public static class TreeNodeControllerTestConfiguration {

		@Bean
		public TreeNodeConverter ldesFragmentConverter(@Value(HOST_NAME_KEY) String hostName, @Autowired TreeNodeStatementCreator treeNodeStatementCreator) {
			PrefixAdder prefixAdder = new PrefixAdderImpl();
			PrefixConstructor prefixConstructor = new PrefixConstructor(hostName, false);
			return new TreeNodeConverterImpl(prefixAdder, prefixConstructor, treeNodeStatementCreator);
		}

		@Bean
		public CachingStrategy cachingStrategy(@Value(HOST_NAME_KEY) String hostName) {
			return new EtagCachingStrategy(hostName);
		}

		@Bean
		public BuildProperties buildProperties() {
			final Properties properties = new Properties();
			properties.setProperty("version", VERSION);
			return new BuildProperties(properties);
		}
	}

	private static Member createMember() {
		final String subject = "http://data.lblod.info/id/public-service-snapshot/46969c9a-a803-ef11-9f8a-000d3aad8fb9";
		final Model model = RDFParser.source("member-utf8-charset.nq").toModel();
		return new Member(subject, model);
	}
}
