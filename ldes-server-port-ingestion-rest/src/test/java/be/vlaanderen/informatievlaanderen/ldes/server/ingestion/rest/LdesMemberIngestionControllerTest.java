package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services.MemberIngestService;
import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.config.IngestionWebConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {LdesMemberIngestionController.class, IngestionWebConfig.class})
class LdesMemberIngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberIngestService memberIngestService;

    @ParameterizedTest(name = "Ingest an LDES member in the REST service using ContentType {0}")
    @ArgumentsSource(ContentTypeRdfFormatLangArgumentsProvider.class)
    void when_POSTRequestIsPerformed_LDesMemberIsSaved(String contentType, RDFFormat rdfFormat, Lang lang) throws Exception {
        String ldesMemberString = readLdesMemberDataFromFile("example-ldes-member.nq",rdfFormat);
        Model ldesMemberData = RdfModelConverter.fromString(ldesMemberString,lang);

        when(memberIngestService.addMember(any())).thenReturn(new LdesMember(RdfModelConverter.fromString(ldesMemberString, lang)));

        mockMvc.perform(post("/mobility-hindrances").contentType(contentType).content(ldesMemberString))
                .andDo(print()).andExpect(status().isOk()).andExpect(result -> {
                    Model responseModel = RDFParserBuilder.create()
                            .fromString(result.getResponse().getContentAsString()).lang(lang).toModel();

                    responseModel.isIsomorphicWith(ldesMemberData);
                });
        verify(memberIngestService, times(1)).addMember(any());
    }

    @Test
    @DisplayName("Requesting using another collection name returns 404")
    void when_POSTRequestIsPerformedUsingAnotherCollectionName_ResponseIs404() throws Exception {
        String ldesMemberString = readLdesMemberDataFromFile("example-ldes-member.nq", RDFFormat.NQUADS);

        mockMvc.perform(post("/another-collection-name")
                        .contentType("application/n-quads")
                        .content(ldesMemberString))
                .andDo(print()).andExpect(status().isNotFound());
    }

    private String readLdesMemberDataFromFile(String fileName, RDFFormat rdfFormat) throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        String content = Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
        return RdfModelConverter.toString(RdfModelConverter.fromString(content, Lang.NQUADS), rdfFormat);
    }

    static class ContentTypeRdfFormatLangArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("application/n-quads", RDFFormat.NQUADS, Lang.NQUADS),
                    Arguments.of("application/n-triples", RDFFormat.NTRIPLES, Lang.NTRIPLES)
            );
        }
    }
}