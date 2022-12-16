package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_NODE_RESOURCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DeletedFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services.TreeNodeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services.TreeNodeConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services.TreeNodeFetcher;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.CachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.caching.EtagCachingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.exceptionhandling.RestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.config.TreeViewWebConfig;

@WebMvcTest
@ActiveProfiles("test")
@Import(TreeNodeControllerTest.TreeNodeControllerTestConfiguration.class)
@ContextConfiguration(classes = { TreeNodeController.class,
		LdesConfig.class, TreeViewWebConfig.class,
		RestResponseEntityExceptionHandler.class })
class TreeNodeControllerTest {

	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private static final String VIEW_NAME = "view";

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private LdesConfig ldesConfig;
	@MockBean
	private TreeNodeFetcher treeNodeFetcher;

	@ParameterizedTest(name = "Correct getting of an open LdesFragment from the  REST Service with mediatype{0}")
	@ArgumentsSource(MediaTypeRdfFormatsArgumentsProvider.class)
	void when_GETRequestIsPerformed_ResponseContainsAnLDesFragment(String mediaType, Lang lang, boolean immutable,
			String expectedHeaderValue) throws Exception {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		TreeNode treeNode = new TreeNode(ldesFragmentRequest.generateFragmentId(), immutable, false, List.of(),
				List.of());

		when(treeNodeFetcher.getFragment(ldesFragmentRequest)).thenReturn(treeNode);

		ResultActions resultActions = mockMvc
				.perform(get("/{collectionName}/{viewName}", ldesConfig.getCollectionName(),
						VIEW_NAME)
						.param("generatedAtTime",
								FRAGMENTATION_VALUE_1)
						.accept(mediaType))
				.andDo(print())
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();
		String headerValue;

		headerValue = result.getResponse().getHeader("Cache-Control");
		assertEquals(expectedHeaderValue, headerValue);

		headerValue = result.getResponse().getHeader("Etag");
		String expectedEtag = "a94b581e9537a12f07470c02a46a30060d6e997c723d1e6b17b0e1b0897f05f8";
		assertNotNull(headerValue);
		assertEquals(expectedEtag, headerValue);

		Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString()).lang(lang)
				.toModel();
		assertEquals(TREE_NODE_RESOURCE, getObjectURI(resultModel,
				RdfConstants.RDF_SYNTAX_TYPE));
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

	@Test
	@DisplayName("Requesting with Unsupported MediaType returns 406")
	void when_GETRequestIsPerformedWithUnsupportedMediaType_ResponseIs406HttpMediaTypeNotAcceptableException()
			throws Exception {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of());
		TreeNode treeNode = new TreeNode(ldesFragmentRequest.generateFragmentId(), false, false, List.of(),
				List.of());
		when(treeNodeFetcher.getFragment(ldesFragmentRequest)).thenReturn(treeNode);

		mockMvc.perform(get("/{collectionName}/{viewName}", ldesConfig.getCollectionName(),
				VIEW_NAME).accept("application/json")).andDo(print())
				.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	void when_GETRequestButMissingFragmentExceptionIsThrown_NotFoundIsReturned()
			throws Exception {

		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of());
		when(treeNodeFetcher.getFragment(ldesFragmentRequest))
				.thenThrow(new MissingFragmentException("fragmentId"));

		ResultActions resultActions = mockMvc
				.perform(get("/{collectionName}/{viewName}", ldesConfig.getCollectionName(),
						VIEW_NAME).accept("application/n-quads"))
				.andDo(print())
				.andExpect(status().isNotFound());
		assertEquals("No fragment exists with fragment identifier: fragmentId",
				resultActions.andReturn().getResponse().getContentAsString());
	}

	@Test
	void when_GETRequestButDeletedFragmentExceptionIsThrown_NotFoundIsReturned()
			throws Exception {

		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of());
		when(treeNodeFetcher.getFragment(ldesFragmentRequest))
				.thenThrow(new DeletedFragmentException("fragmentId"));

		ResultActions resultActions = mockMvc
				.perform(get("/{collectionName}/{viewName}", ldesConfig.getCollectionName(),
						VIEW_NAME).accept("application/n-quads"))
				.andDo(print())
				.andExpect(status().isGone());
		assertEquals("Fragment with following identifier has been deleted: fragmentId",
				resultActions.andReturn().getResponse().getContentAsString());
	}

	@Test
	@DisplayName("Requesting using another collection name returns 404")
	void when_GETRequestIsPerformedOnOtherCollectionName_ResponseIs404() throws Exception {
		mockMvc.perform(get("/")
				.param("generatedAtTime",
						FRAGMENTATION_VALUE_1)
				.accept("application/n-quads")).andDo(print())
				.andExpect(status().isNotFound());
	}

	static class MediaTypeRdfFormatsArgumentsProvider implements
			ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("application/n-quads", Lang.NQUADS, true, "public, max-age=604800, immutable"),
					Arguments.of("application/ld+json", Lang.JSONLD11, true, "public, max-age=604800, immutable"),
					Arguments.of("application/turtle", Lang.TURTLE, false, "public, max-age=60"),
					Arguments.of("*/*", Lang.TURTLE, false, "public, max-age=60"));
		}
	}

	@TestConfiguration
	public static class TreeNodeControllerTestConfiguration {

		@Bean
		public TreeNodeConverter ldesFragmentConverter(final LdesConfig ldesConfig) {
			PrefixAdder prefixAdder = new PrefixAdderImpl();
			return new TreeNodeConverterImpl(prefixAdder, ldesConfig);
		}

		@Bean
		public CachingStrategy cachingStrategy(final LdesConfig ldesConfig) {
			return new EtagCachingStrategy(ldesConfig);
		}
	}
}