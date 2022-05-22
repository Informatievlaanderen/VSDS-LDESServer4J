package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentProvider;
import org.hamcrest.Matchers;
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
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LdesFragmentController.class)
class LdesFragmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FragmentProvider fragmentProvider;

    @Test
    @DisplayName("Correct getting of an LdesFragment from the REST Service")
    void when_GETRequestIsPerformed_ResponseContainsAnLDesFragment() throws Exception {
        LdesMember ldesMember = readLdesMemberFromFile("example-ldes-member.txt");
        List<LdesMember> ldesMembers = List.of(ldesMember);
        when(fragmentProvider.getFragment()).thenReturn(new LdesFragment(ldesMembers));

        mockMvc.perform(get("/ldes-fragment")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"@id\":\"test-id\"")))
                .andExpect(content().string(Matchers.containsString("\"@context\":\"test-context\"")))
                .andExpect(content().string(Matchers.containsString("\"items\":[[{")));
        verify(fragmentProvider, times(1)).getFragment();
    }

    private LdesMember readLdesMemberFromFile(String fileName) throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return new LdesMember(Files.lines(Paths.get(file.toURI())).toArray(String[]::new));
    }
}