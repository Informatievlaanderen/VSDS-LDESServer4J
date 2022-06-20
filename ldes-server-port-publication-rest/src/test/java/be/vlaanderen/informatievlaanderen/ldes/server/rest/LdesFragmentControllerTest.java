package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentProvider;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ResourceFactory.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LdesFragmentController.class)
class LdesFragmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FragmentProvider fragmentProvider;

    private final LdesFragmentConfig ldesFragmentConfig = new LdesFragmentConfig();

    @BeforeEach
    void setup() {
        ldesFragmentConfig.setView("http://localhost:8089/exampleData");
        ldesFragmentConfig.setShape("http://localhost:8089/exampleData/shape");
    }

    @ParameterizedTest(name = "Correct getting of an LdesFragment from the REST Service with mediatype {0}")
    @ArgumentsSource(MediaTypeRdfFormatsArgumentsProvider.class)
    void when_GETRequestIsPerformed_ResponseContainsAnLDesFragment(String mediaType, Lang lang) throws Exception {
        LdesMember ldesMember = readLdesMemberFromFile("example-ldes-member.nq");
        List<LdesMember> ldesMembers = List.of(ldesMember);
        when(fragmentProvider.getFragment()).thenReturn(new LdesFragment(ldesMembers, ldesFragmentConfig.toMap()));

        ResultActions resultActions = mockMvc.perform(get("/ldes-fragment").accept(mediaType)).andDo(print())
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        Model resultModel = RDFParserBuilder.create().fromString(result.getResponse().getContentAsString()).lang(lang)
                .toModel();
        assertTrue(resultModel.contains(createStatement(createResource(ldesFragmentConfig.getView()),
                createProperty("https://w3id.org/tree#shape"), createResource(ldesFragmentConfig.getShape()))));
        verify(fragmentProvider, times(1)).getFragment();
    }

    @Test
    void when_GETRequestIsPerformedWithUnsupportedMediaType_ResponseIs406HttpMediaTypeNotAcceptableException()
            throws Exception {
        LdesMember ldesMember = readLdesMemberFromFile("example-ldes-member.nq");
        List<LdesMember> ldesMembers = List.of(ldesMember);
        when(fragmentProvider.getFragment()).thenReturn(new LdesFragment(ldesMembers, ldesFragmentConfig.toMap()));

        mockMvc.perform(get("/ldes-fragment").accept("application/json")).andDo(print())
                .andExpect(status().is4xxClientError());

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