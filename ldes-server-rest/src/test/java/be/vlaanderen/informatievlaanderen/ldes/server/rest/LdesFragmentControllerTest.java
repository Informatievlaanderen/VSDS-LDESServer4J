package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.LdesFragmentService;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LdesFragmentController.class)
class LdesFragmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LdesFragmentService ldesFragmentService;

    @Test
    @DisplayName("Correct posting of a LdesFragment to the REST Service")
    void when_POSTRequestIsPerformed_LDesFragmentIsStored() throws Exception {
        JSONObject originalLdesFragment = new JSONObject(Map.of("data", "some_ldes_data"));
        JSONObject expectedStoredLdesFragment = new JSONObject(Map.of("data", "stored_some_ldes_data"));
        when(ldesFragmentService.storeLdesFragment(originalLdesFragment)).thenReturn(expectedStoredLdesFragment);

        mockMvc.perform(post("/ldes-fragment").contentType(MediaType.APPLICATION_JSON)
                .content(originalLdesFragment.toJSONString())).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(expectedStoredLdesFragment.toJSONString()));

        verify(ldesFragmentService, times(1)).storeLdesFragment(originalLdesFragment);
    }

    @Test
    @DisplayName("Correct getting of an LdesFragment from the REST Service")
    void when_GETRequestIsPerformed_ResponseContainsAnLDesFragment() throws Exception {
        JSONObject returnedLdesFragment = new JSONObject(Map.of("data", "some_ldes_data"));
        when(ldesFragmentService.retrieveLdesFragmentsPage(0)).thenReturn(returnedLdesFragment);

        mockMvc.perform(get("/ldes-fragment")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(returnedLdesFragment.toJSONString()));

        verify(ldesFragmentService, times(1)).retrieveLdesFragmentsPage(0);
    }
}