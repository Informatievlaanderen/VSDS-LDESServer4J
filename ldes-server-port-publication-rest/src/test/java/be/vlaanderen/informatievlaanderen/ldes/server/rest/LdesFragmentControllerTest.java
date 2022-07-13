package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LdesFragmentController.class)
@ActiveProfiles("test")
class LdesFragmentControllerTest {
    private static final String LDES_EVENTSTREAM = "https://w3id.org/ldes#EventStream";
    private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
    private static final String FRAGMENT_ID = "http://localhost:8080/mobility-hindrances?generatedAtTime=" + FRAGMENTATION_VALUE_1;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ViewConfig viewConfig;

    @Autowired
    private LdesConfig ldesConfig;

    @MockBean
    private FragmentationService fragmentationService;

    @Test
    @DisplayName("Correct getting of an initial dummy LdesFragment")
    void when_GETRequestIsPerformedOnBaseURLAndNoFragmentIsCreatedYet_ResponseContainsADummyLDesFragment() throws Exception {
        String fragmentId = "%s/%s?generatedAtTime=%s".formatted(ldesConfig.getHostName(), ldesConfig.getCollectionName(), FRAGMENTATION_VALUE_1);
        LdesFragment dummyFragment = new LdesFragment(fragmentId, new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()), viewConfig.getShape(), ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), null));
        when(fragmentationService.getInitialFragment(ldesConfig.getCollectionName(), viewConfig.getTimestampPath())).thenReturn(dummyFragment);

        ResultActions resultActions = mockMvc
                .perform(get("/{viewShortName}", ldesConfig.getCollectionName()))
                .andDo(print())
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String headerValue = result.getResponse().getHeader("Cache-Control");
        assertEquals("public, max-age=60", headerValue);
        Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString()).lang(Lang.JSONLD11)
                .toModel();
        assertEquals(viewConfig.getShape(), getObjectURI(resultModel, RdfConstants.TREE_SHAPE));
        assertEquals(viewConfig.getTimestampPath(), getObjectURI(resultModel, RdfConstants.LDES_TIMESTAMP_PATH));
        assertEquals(viewConfig.getVersionOfPath(), getObjectURI(resultModel, RdfConstants.LDES_VERSION_OF));
        assertEquals(LDES_EVENTSTREAM, getObjectURI(resultModel, RdfConstants.RDF_SYNTAX_TYPE));
        verify(fragmentationService, times(1)).getInitialFragment(ldesConfig.getCollectionName(), viewConfig.getTimestampPath());
        verifyNoMoreInteractions(fragmentationService);
    }

    @Test
    @DisplayName("Correct redirecting to first fragment")
    void when_GETRequestIsPerformedOnBaseURLAndFragmentIsAvailable_FragmentIsReturnedAndReponseContainsRedirect() throws Exception {
        String fragmentId = "%s/%s?generatedAtTime=%s".formatted(ldesConfig.getHostName(), ldesConfig.getCollectionName(), FRAGMENTATION_VALUE_1);
        LdesFragment realFragment = new LdesFragment(fragmentId, new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()), viewConfig.getShape(), ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), FRAGMENTATION_VALUE_1));
        when(fragmentationService.getInitialFragment(ldesConfig.getCollectionName(), viewConfig.getTimestampPath())).thenReturn(realFragment);

        ResultActions resultActions = mockMvc
                .perform(get("/{viewShortName}", ldesConfig.getCollectionName()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        MvcResult result = resultActions.andReturn();
        String headerValue = result.getResponse().getHeader("Cache-Control");
        assertEquals("public, max-age=60", headerValue);
        Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString()).lang(Lang.JSONLD11)
                .toModel();
        assertEquals(viewConfig.getShape(), getObjectURI(resultModel, RdfConstants.TREE_SHAPE));
        assertEquals(viewConfig.getTimestampPath(), getObjectURI(resultModel, RdfConstants.LDES_TIMESTAMP_PATH));
        assertEquals(viewConfig.getVersionOfPath(), getObjectURI(resultModel, RdfConstants.LDES_VERSION_OF));
        assertEquals(fragmentId, getObjectURI(resultModel, RdfConstants.TREE_VIEW));
        assertEquals(LDES_EVENTSTREAM, getObjectURI(resultModel, RdfConstants.RDF_SYNTAX_TYPE));
        verify(fragmentationService, times(1)).getInitialFragment(ldesConfig.getCollectionName(), viewConfig.getTimestampPath());
        verifyNoMoreInteractions(fragmentationService);
    }

    @Test
    @DisplayName("Correct returning a complete fragment")
    void when_GETRequestIsPerformeOnBaseURLAndFragmentIsAvailable_FragmentIsReturnedAndReponseContainsRedirect() throws Exception {
        String fragmentId = "%s/%s?generatedAtTime=%s".formatted(ldesConfig.getHostName(), ldesConfig.getCollectionName(), FRAGMENTATION_VALUE_1);
        FragmentInfo fragmentInfo = new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()), viewConfig.getShape(), ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), FRAGMENTATION_VALUE_1);
        fragmentInfo.setImmutable(true);
        LdesFragment realFragment = new LdesFragment(fragmentId, fragmentInfo);
        when(fragmentationService.getFragment(ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), FRAGMENTATION_VALUE_1)).thenReturn(realFragment);

        ResultActions resultActions = mockMvc
                .perform(get("/{viewShortName}", ldesConfig.getCollectionName())
                        .param("generatedAtTime", FRAGMENTATION_VALUE_1))
                .andDo(print())
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String headerValue = result.getResponse().getHeader("Cache-Control");
        assertEquals("public, max-age=604800, immutable", headerValue);
        Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString()).lang(Lang.JSONLD11)
                .toModel();
        assertEquals(viewConfig.getShape(), getObjectURI(resultModel, RdfConstants.TREE_SHAPE));
        assertEquals(viewConfig.getTimestampPath(), getObjectURI(resultModel, RdfConstants.LDES_TIMESTAMP_PATH));
        assertEquals(viewConfig.getVersionOfPath(), getObjectURI(resultModel, RdfConstants.LDES_VERSION_OF));
        assertEquals(fragmentId, getObjectURI(resultModel, RdfConstants.TREE_VIEW));
        assertEquals(LDES_EVENTSTREAM, getObjectURI(resultModel, RdfConstants.RDF_SYNTAX_TYPE));
        verify(fragmentationService, times(1)).getFragment(ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), FRAGMENTATION_VALUE_1);
        verifyNoMoreInteractions(fragmentationService);
    }

    @ParameterizedTest(name = "Correct getting of an open LdesFragment from the REST Service with mediatype {0}")
    @ArgumentsSource(MediaTypeRdfFormatsArgumentsProvider.class)
    void when_GETRequestIsPerformed_ResponseContainsAnLDesFragment(String mediaType, Lang lang) throws Exception {
        String fragmentId = "%s/%s?generatedAtTime=%s".formatted(ldesConfig.getHostName(), ldesConfig.getCollectionName(), FRAGMENTATION_VALUE_1);
        LdesFragment ldesFragment = new LdesFragment(fragmentId, new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()), viewConfig.getShape(), ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), FRAGMENTATION_VALUE_1));

        when(fragmentationService.getFragment(ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), FRAGMENTATION_VALUE_1)).thenReturn(ldesFragment);

        ResultActions resultActions = mockMvc.perform(get("/{viewShortName}", ldesConfig.getCollectionName())
                        .param("generatedAtTime", FRAGMENTATION_VALUE_1).accept(mediaType)).andDo(print())
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString()).lang(lang)
                .toModel();
        assertEquals(viewConfig.getShape(), getObjectURI(resultModel, RdfConstants.TREE_SHAPE));
        assertEquals(viewConfig.getTimestampPath(), getObjectURI(resultModel, RdfConstants.LDES_TIMESTAMP_PATH));
        assertEquals(viewConfig.getVersionOfPath(), getObjectURI(resultModel, RdfConstants.LDES_VERSION_OF));
        assertEquals(FRAGMENT_ID, getObjectURI(resultModel, RdfConstants.TREE_VIEW));
        assertEquals(LDES_EVENTSTREAM, getObjectURI(resultModel, RdfConstants.RDF_SYNTAX_TYPE));
        verify(fragmentationService, times(1)).getFragment(ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), FRAGMENTATION_VALUE_1);
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
        String fragmentId = "%s/%s?generatedAtTime=%s".formatted(ldesConfig.getHostName(), ldesConfig.getCollectionName(), FRAGMENTATION_VALUE_1);
        LdesFragment ldesFragment = new LdesFragment(fragmentId, new FragmentInfo(null, null, null, null, null));

        when(fragmentationService.getFragment(ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), FRAGMENTATION_VALUE_1)).thenReturn(ldesFragment);

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
                    Arguments.of("application/ld+json", Lang.JSONLD11));
        }
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ViewConfig viewConfig() {
            ViewConfig viewConfig = new ViewConfig();
            viewConfig.setShape("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape");
            viewConfig.setMemberLimit(3L);
            viewConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
            viewConfig.setVersionOfPath("http://purl.org/dc/terms/isVersionOf");
            return viewConfig;
        }

        @Bean
        public LdesConfig ldesConfig() {
            LdesConfig ldesConfig = new LdesConfig();
            ldesConfig.setCollectionName("mobility-hindrances");
            ldesConfig.setHostName("http://localhost:8080");
            return ldesConfig;
        }

    }
}