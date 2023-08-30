package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.exception.SnapshotCreationException;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services.SnapshotService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test" })
@ContextConfiguration(classes = { SnapshotController.class,
		AdminRestResponseEntityExceptionHandler.class })
class SnapshotControllerTest {
	private static final String COLLECTION = "mobility-hindrances";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SnapshotService snapshotService;

	@Disabled("Snapshotting is currently not supported")
	@Test
	void when_SnapshotIsCreated_OKIsReturned() throws Exception {
		mockMvc.perform(post("/admin/api/v1/{collection}/snapshots", COLLECTION))
				.andExpect(status().isOk());

		verify(snapshotService, times(1)).createSnapshot(COLLECTION);
	}

	@Disabled("Snapshotting is currently not supported")
	@Test
	void when_SnapshotCannotBeCreated_500IsReturnedWithErrorMessage() throws Exception {
		doThrow(new SnapshotCreationException("cause")).when(snapshotService).createSnapshot(COLLECTION);

		ResultActions resultActions = mockMvc.perform(post("/admin/api/v1/{collection}/snapshots", COLLECTION))
				.andExpect(status().is5xxServerError());
		String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
		assertEquals("Unable to create snapshot.\nCause: cause", contentAsString);
	}

}
