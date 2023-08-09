package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.DcatViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNodeDto;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.services.TreeNodeFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.TreeMemberList;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.TreeNodeInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.caching.EtagCachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.config.RestConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.exceptionhandling.RestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.TreeNodeController;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.config.TreeViewWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.services.TreeNodeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.services.TreeNodeConverterImpl;
import org.apache.http.HttpHeaders;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.HOST_NAME_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@Import(TreeNodeControllerTest.TreeNodeControllerTestConfiguration.class)
@ContextConfiguration(classes = { TreeNodeController.class,
		RestConfig.class, TreeViewWebConfig.class,
		RestResponseEntityExceptionHandler.class })
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
		TreeNodeInfo treeNodeInfo = new TreeNodeInfo(fragmentId, List.of());
		TreeMemberList treeMemberList = new TreeMemberList(COLLECTION_NAME, List.of());
		TreeNodeDto treeNodeDto = new TreeNodeDto(new TreeNode(treeNodeInfo, treeMemberList), fragmentId,
				List.of(), List.of(), immutable, false,
				COLLECTION_NAME);

		when(treeNodeFetcher.getFragment(ldesFragmentRequest)).thenReturn(treeNodeDto);

		ResultActions resultActions = mockMvc
				.perform(get("/{collectionName}/{viewName}", COLLECTION_NAME, VIEW_NAME)
						.param("generatedAtTime", FRAGMENTATION_VALUE_1)
						.accept(mediaType))
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();
		String headerValue;

		headerValue = result.getResponse().getHeader("Cache-Control");
		assertEquals(expectedHeaderValue, headerValue);

		headerValue = Objects.requireNonNull(result.getResponse().getHeader("Etag"))
				.replace("\"", "");

		assertNotNull(headerValue);
		assertEquals(expectedEtag, headerValue);

		Integer maxAge = extractMaxAge(result);
		assertNotNull(maxAge);

		if (immutable) {
			assertEquals(CONFIGURED_MAX_AGE_IMMUTABLE, maxAge);
		} else {
			assertEquals(CONFIGURED_MAX_AGE, maxAge);
		}

		Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString()).lang(lang)
				.toModel();
		assertEquals(TREE_NODE_RESOURCE, getObjectURI(resultModel,
				RDF_SYNTAX_TYPE));
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
	void when_GETRequestIsPerformedWithUnsupportedMediaType_ResponseIs406HttpMediaTypeNotAcceptableException()
			throws Exception {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(
				ViewName.fromString(fullViewName), List.of());
		final String fragmentId = new LdesFragmentIdentifier(ldesFragmentRequest.viewName(),
				ldesFragmentRequest.fragmentPairs())
				.asString();
		TreeNodeDto treeNodeDto = new TreeNodeDto(null, fragmentId, List.of(), List.of(), false, false,
				COLLECTION_NAME);
		when(treeNodeFetcher.getFragment(ldesFragmentRequest)).thenReturn(treeNodeDto);
		mockMvc.perform(get("/{collectionName}/{viewName}", COLLECTION_NAME, VIEW_NAME)
				.accept("application/json"))
				.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	void when_GETRequestButMissingFragmentExceptionIsThrown_NotFoundIsReturned()
			throws Exception {

		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(
				ViewName.fromString(fullViewName),
				List.of());
		when(treeNodeFetcher.getFragment(ldesFragmentRequest))
				.thenThrow(new MissingFragmentException("fragmentId"));

		ResultActions resultActions = mockMvc
				.perform(get("/{collectionName}/{viewName}", COLLECTION_NAME,
						VIEW_NAME).accept("application/n-quads"))
				.andExpect(status().isNotFound());
		assertEquals("No fragment exists with fragment identifier: fragmentId",
				resultActions.andReturn().getResponse().getContentAsString());
	}

	@Test
	@DisplayName("Requesting using another collection name returns 404")
	void when_GETRequestIsPerformedOnOtherCollectionName_ResponseIs404() throws Exception {
		mockMvc.perform(get("/")
				.param("generatedAtTime",
						FRAGMENTATION_VALUE_1)
				.accept("application/n-quads"))
				.andExpect(status().isNotFound());
	}

	static class MediaTypeRdfFormatsArgumentsProvider implements
			ArgumentsProvider {

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
							"c7ea36907e9d946b78513ef4f5e30002a4d3be1b675589727a8516452e74fea8"),
					Arguments.of("text/html", Lang.TURTLE, false, "public,max-age=" + CONFIGURED_MAX_AGE,
							"eab5179ac011c835cb460a0bdc6a28a52491255197a1073d2b963675961e66f2"));
		}
	}

	@TestConfiguration
	public static class TreeNodeControllerTestConfiguration {

		@Bean
		public TreeNodeConverter ldesFragmentConverter(@Value(HOST_NAME_KEY) String hostName) {
			PrefixAdder prefixAdder = new PrefixAdderImpl();
			return new TreeNodeConverterImpl(prefixAdder, hostName, mock(DcatViewService.class));
		}

		@Bean
		public CachingStrategy cachingStrategy(@Value(HOST_NAME_KEY) String hostName) {
			return new EtagCachingStrategy(hostName);
		}
	}
}
