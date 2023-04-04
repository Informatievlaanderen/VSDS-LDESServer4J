package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services.SnapshotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = { SnapshotController.class })
class SnapshotControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SnapshotService snapshotService;

	@Test
	void test() throws Exception {
		mockMvc.perform(post("/admin/api/v1/snapshots"))
				.andDo(print()).andExpect(status().isOk());

		verify(snapshotService, times(1)).createSnapshot();
	}

}