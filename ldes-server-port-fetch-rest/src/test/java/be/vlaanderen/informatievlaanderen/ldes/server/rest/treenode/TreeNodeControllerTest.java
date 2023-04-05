package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentFetchService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.LdesFragmentConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.LdesFragmentConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.exceptionhandling.RestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.config.TreeViewWebConfig;
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

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_NODE_RESOURCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles("test")
@Import(TreeNodeControllerTest.TreeNodeControllerTestConfiguration.class)
@ContextConfiguration(classes = { TreeNodeController.class,
		LdesConfig.class, TreeViewWebConfig.class, RestResponseEntityExceptionHandler.class })
class TreeNodeControllerTest {
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private static final String VIEW_NAME = "view";

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private FragmentFetchService fragmentFetchService;

	@ParameterizedTest(name = "Correct getting of an open LdesFragment from the REST Service with mediatype{0}")
	@ArgumentsSource(MediaTypeRdfFormatsArgumentsProvider.class)
	void when_GETRequestIsPerformed_ResponseContainsAnLDesFragment(String mediaType, Lang lang, boolean immutable,
			String expectedHeaderValue) throws

	Exception {
		LdesFragment ldesFragment = new LdesFragment(new FragmentInfo(
				VIEW_NAME, List.of(new FragmentPair(GENERATED_AT_TIME,
						FRAGMENTATION_VALUE_1))));
		ldesFragment.setImmutable(immutable);
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		when(fragmentFetchService.getFragment(ldesFragmentRequest)).thenReturn(ldesFragment);

		ResultActions resultActions = mockMvc.perform(get("/{viewName}",
				VIEW_NAME)
				.param("generatedAtTime",
						FRAGMENTATION_VALUE_1)
				.accept(mediaType)).andDo(print())
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();
		String headerValue = result.getResponse().getHeader("Cache-Control");
		assertEquals(expectedHeaderValue, headerValue);
		Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString()).lang(lang)
				.toModel();
		assertEquals(TREE_NODE_RESOURCE, getObjectURI(resultModel,
				RdfConstants.RDF_SYNTAX_TYPE));
		verify(fragmentFetchService, times(1)).getFragment(ldesFragmentRequest);
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
		LdesFragment ldesFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of()));

		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of());
		when(fragmentFetchService.getFragment(ldesFragmentRequest)).thenReturn(ldesFragment);

		mockMvc.perform(get("/{viewName}",
				VIEW_NAME).accept("application/json")).andDo(print())
				.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	void when_GETRequestButMissingFragmentExceptionIsThrown_NotFoundIsReturned()
			throws Exception {

		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of());
		when(fragmentFetchService.getFragment(ldesFragmentRequest))
				.thenThrow(new MissingFragmentException("fragmentId"));

		ResultActions resultActions = mockMvc.perform(get("/view").accept("application/n-quads")).andDo(print())
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
		public LdesFragmentConverter ldesFragmentConverter(final LdesConfig ldesConfig) {
			MemberRepository memberRepository = mock(MemberRepository.class);
			PrefixAdder prefixAdder = new PrefixAdderImpl();
			return new LdesFragmentConverterImpl(memberRepository, prefixAdder, ldesConfig);
		}
	}
}