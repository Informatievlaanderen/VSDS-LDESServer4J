package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentationService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LdesFragmentController.class)
class LdesFragmentControllerTest {
    public static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ViewConfig viewConfig;

    @MockBean
    private FragmentationService fragmentationService;
    private final LdesConfig ldesConfig = new LdesConfig();

    @BeforeEach
    void setup() {
        ldesConfig.setCollectionName("h");
        ldesConfig.setHostName("i");
    }

    @ParameterizedTest(name = "Correct getting of an LdesFragment from the REST Service with mediatype {0}")
    @ArgumentsSource(MediaTypeRdfFormatsArgumentsProvider.class)
    void when_GETRequestIsPerformed_ResponseContainsAnLDesFragment(String mediaType, Lang lang) throws Exception {
        String fragmentId = "%s/%s?generatedAtTime=%s".formatted(ldesConfig.getHostName(), ldesConfig.getCollectionName(), FRAGMENTATION_VALUE_1);
        LdesFragment ldesFragment = new LdesFragment(fragmentId, new FragmentInfo(null, null, null, null, null, null, null));

        when(fragmentationService.getFragment(ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), FRAGMENTATION_VALUE_1)).thenReturn(ldesFragment);

        ResultActions resultActions = mockMvc.perform(get("/{viewShortName}", ldesConfig.getCollectionName())
                        .param("generatedAtTime", FRAGMENTATION_VALUE_1).accept(mediaType)).andDo(print())
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString()).lang(lang)
                .toModel();
        //TODO
//        assertTrue(resultModel.contains(createStatement(createResource(ldesFragmentConfig.getView()),
//                createProperty("https://w3id.org/tree#shape"), createResource(ldesFragmentConfig.getShape()))));
        verify(fragmentationService, times(1)).getFragment(ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), FRAGMENTATION_VALUE_1);
    }

    @Test
    void when_GETRequestIsPerformedWithUnsupportedMediaType_ResponseIs406HttpMediaTypeNotAcceptableException()
            throws Exception {
       /* LdesMember ldesMember = readLdesMemberFromFile("example-ldes-member.nq");
        List<LdesMember> ldesMembers = List.of(ldesMember);
        when(fragmentProvider.getFragment()).thenReturn(new LdesFragment(ldesMembers, ldesFragmentConfig.toMap()));

        mockMvc.perform(get("/ldes-fragment").accept("application/json")).andDo(print())
                .andExpect(status().is4xxClientError());*/

    }

    private LdesMember readLdesMemberFromFile(String fileName) throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());

        Model ldesModel = RDFParserBuilder.create()
                .fromString(Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"))).lang(Lang.NQUADS)
                .toModel();

        return new LdesMember(ldesModel);
    }

    static class MediaTypeRdfFormatsArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("application/n-quads", Lang.NQUADS),
                    Arguments.of("application/ld+json", Lang.JSONLD11));
        }
    }
}