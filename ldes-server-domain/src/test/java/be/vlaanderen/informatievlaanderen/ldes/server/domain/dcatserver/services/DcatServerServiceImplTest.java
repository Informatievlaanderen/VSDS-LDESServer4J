package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.exceptions.MissingDcatServerException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DcatAlreadyConfiguredException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DcatServerServiceImplTest {
	private static final String ID = "id";
	private static final Model DCAT = ModelFactory.createDefaultModel();
	private static final DcatServer SERVER_DCAT = new DcatServer(ID, DCAT);
	private DcatServerService service;
	@Mock
	private DcatServerRepository repository;

	@BeforeEach
	void setUp() {
		service = new DcatServerServiceImpl(repository);
	}

	@Test
	void when_ServerHasNoDcat_and_CreateDcat_then_ReturnIt() {
		when(repository.getServerDcat()).thenReturn(List.of());
		when(repository.saveServerDcat(any())).thenReturn(SERVER_DCAT);

		final DcatServer createdDcat = assertDoesNotThrow(() -> service.createDcatServer(DCAT));

		assertEquals(SERVER_DCAT, createdDcat);
		InOrder inOrder = Mockito.inOrder(repository);
		inOrder.verify(repository).getServerDcat();
		inOrder.verify(repository).saveServerDcat(any());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_ServerAlreadyHasDcatConfigured_then_ThrowException() {
		when(repository.getServerDcat()).thenReturn(List.of(SERVER_DCAT));
		final String expectedMessage = "The server can contain only one dcat configuration and there already has been configured one with id " + ID;

		Exception e = assertThrows(DcatAlreadyConfiguredException.class, () -> service.createDcatServer(DCAT));

		assertEquals(expectedMessage, e.getMessage());
		verify(repository).getServerDcat();
		verifyNoMoreInteractions(repository);
	}

	@Test
	void when_UpdateExistingDcat_then_ReturnDcat() {
		when(repository.getServerDcatById(ID)).thenReturn(Optional.of(SERVER_DCAT));
		when(repository.saveServerDcat(SERVER_DCAT)).thenReturn(SERVER_DCAT);

		DcatServer updatedDcatServer = assertDoesNotThrow(() -> service.updateDcatServer(ID, DCAT));

		assertEquals(SERVER_DCAT, updatedDcatServer);
		InOrder inOrder = inOrder(repository);
		inOrder.verify(repository).getServerDcatById(ID);
		inOrder.verify(repository).saveServerDcat(SERVER_DCAT);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_UpdateNonExistingDcat_then_ThrowException() {
		when(repository.getServerDcatById(ID)).thenReturn(Optional.empty());
		String expectedMessage = String.format("No dcat is configured on the server with id %s", ID);

		Exception e = assertThrows(MissingDcatServerException.class, () -> service.updateDcatServer(ID, DCAT));

		assertEquals(expectedMessage, e.getMessage());
		verify(repository).getServerDcatById(ID);
		verifyNoMoreInteractions(repository);
	}

	@Test
	void when_DeleteExistingDcat_then_ReturnVoid() {
		assertDoesNotThrow(() -> service.deleteDcatServer(ID));
		verify(repository).deleteServerDcat(ID);
		verifyNoMoreInteractions(repository);
	}
}
