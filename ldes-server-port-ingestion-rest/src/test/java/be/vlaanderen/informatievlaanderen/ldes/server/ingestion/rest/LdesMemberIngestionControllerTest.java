package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentationService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LdesMemberIngestionController.class)
class LdesMemberIngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FragmentationService fragmentationService;

    @Test
    @DisplayName("Ingest an LDES member in the REST service")
    void when_POSTRequestIsPerformed_LDesMemberIsSaved() throws Exception {
        String ldesMemberString = readLdesMemberDataFromFile("example-ldes-member.nq");
        Model ldesMemberData = RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS);

        when(fragmentationService.addMember(any())).thenReturn(new LdesMember(RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS)));

        mockMvc.perform(post("/ldes-member").contentType("application/n-quads").content(ldesMemberString))
                .andDo(print()).andExpect(status().isOk()).andExpect(result -> {
                    Model responseModel = RDFParserBuilder.create()
                            .fromString(result.getResponse().getContentAsString()).lang(Lang.NQUADS).toModel();

                    responseModel.isIsomorphicWith(ldesMemberData);
                });
        verify(fragmentationService, times(1)).addMember(any());
    }

    private String readLdesMemberDataFromFile(String fileName) throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
    }

}