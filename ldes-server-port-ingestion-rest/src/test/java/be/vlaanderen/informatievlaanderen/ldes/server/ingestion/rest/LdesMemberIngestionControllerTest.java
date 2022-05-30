package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.SdsReader;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LdesMemberIngestionController.class)
class LdesMemberIngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SdsReader sdsReader;

    @Test
    @DisplayName("Ingest an LDES member in the REST service")
    void when_POSTRequestIsPerformed_LDesMemberIsSaved() throws Exception {
        String ldesMemberData = readLdesMemberDataFromFile("example-ldes-member.txt");
        when(sdsReader.storeLdesMember(any())).thenReturn(new LdesMember(ldesMemberData.split("\n")));

        mockMvc.perform(post("/ldes-member").contentType("application/n-quads").content(ldesMemberData)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(ldesMemberData));
        verify(sdsReader, times(1)).storeLdesMember(any());
    }

    private String readLdesMemberDataFromFile(String fileName) throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return Files.lines(Paths.get(file.toURI())).toString();
    }
}