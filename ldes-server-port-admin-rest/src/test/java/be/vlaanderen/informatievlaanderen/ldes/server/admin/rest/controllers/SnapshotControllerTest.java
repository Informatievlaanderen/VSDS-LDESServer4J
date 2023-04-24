package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.exception.SnapshotCreationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services.SnapshotService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test" })
@ContextConfiguration(classes = { SnapshotController.class,
		AdminRestResponseEntityExceptionHandler.class, AppConfig.class })
class SnapshotControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SnapshotService snapshotService;

	@Autowired
	private AppConfig appConfig;

	@Test
	void when_SnapshotIsCreated_OKIsReturned() throws Exception {
		LdesConfig ldesConfig = appConfig.getCollections().get(0);
		mockMvc.perform(post("/admin/api/v1/{collection}/snapshots", ldesConfig.getCollectionName()))
				.andDo(print()).andExpect(status().isOk());

		verify(snapshotService, times(1)).createSnapshot(ldesConfig);
	}

	@Test
	void when_SnapshotCannotBeCreated_500IsReturnedWithErrorMessage() throws Exception {
		LdesConfig ldesConfig = appConfig.getCollections().get(0);

		doThrow(new SnapshotCreationException("cause")).when(snapshotService).createSnapshot(ldesConfig);

		ResultActions resultActions = mockMvc.perform(post("/admin/api/v1/{collection}/snapshots",
				ldesConfig.getCollectionName()))
				.andDo(print()).andExpect(status().is5xxServerError());
		String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
		assertEquals("Unable to create snapshot.\nCause: cause", contentAsString);
	}

}
