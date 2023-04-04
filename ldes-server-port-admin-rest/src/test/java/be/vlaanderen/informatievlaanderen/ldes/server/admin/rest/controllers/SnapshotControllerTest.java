package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesConfigException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.exception.SnapshotCreationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services.SnapshotService;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = { SnapshotController.class,
		AdminRestResponseEntityExceptionHandler.class })
class SnapshotControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SnapshotService snapshotService;

	@Test
	void when_SnapshotIsCreated_OKIsReturned() throws Exception {
		mockMvc.perform(post("/admin/api/v1/snapshots"))
				.andDo(print()).andExpect(status().isOk());

		verify(snapshotService, times(1)).createSnapshot();
	}

	@Test
	void when_SnapshotCannotBeCreated_500IsReturnedWithErrorMessage() throws Exception {
		doThrow(new SnapshotCreationException("cause")).when(snapshotService).createSnapshot();

		ResultActions resultActions = mockMvc.perform(post("/admin/api/v1/snapshots"))
				.andDo(print()).andExpect(status().is5xxServerError());
		String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
		assertEquals("Unable to create snapshot.\nCause: cause", contentAsString);
	}

}