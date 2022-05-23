package be.vlaanderen.informatievlaanderen.vsds.server.port.ingestion;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.SdsReader;

@WebMvcTest(controllers=LdesMemberIngestionController.class)
public class LdesMemberIngestionControllerTest {
	
	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private SdsReader sdsReader;

    @Test
    @DisplayName("Ingest an LDES member in the REST service")
    void when_POSTRequestIsPerformed_LDesMemberIsSaved() throws Exception {
        String ldesMemberData = readLdesMemberDataFromFile("example-ldes-member.txt");

        mockMvc.perform(post("/ldes-fragment").content(ldesMemberData).contentType("application/n-quads"));
    }

    private String readLdesMemberDataFromFile(String fileName) throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return Files.lines(Paths.get(file.toURI())).toString();
    }
}
