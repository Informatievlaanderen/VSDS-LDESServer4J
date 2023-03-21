package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest;

import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
class AdminRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void when_ModelInRequestBody_Then_ModelIsConverted() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/{viewName}")
                        .accept(String.valueOf(Lang.TURTLE)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void retrieveAllLdesStreams() {
    }

    @Test
    void putLdesStream() {
    }

    @Test
    void getLdesStream() {
    }

    @Test
    void getShape() {
    }

    @Test
    void putShape() {
    }

    @Test
    void getViews() {
    }

    @Test
    void putViews() {
    }

    @Test
    void getView() {
    }
}