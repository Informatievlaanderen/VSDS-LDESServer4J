package be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DcatAlreadyConfiguredException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingServerDcatException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.entities.ServerDcat;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.repositories.ServerDcatRepository;
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
class ServerDcatServiceImplTest {
	private static final String ID = "id";
	private static final Model DCAT = ModelFactory.createDefaultModel();
	private static final ServerDcat SERVER_DCAT = new ServerDcat(ID, DCAT);
	private ServerDcatService service;
	@Mock
	private ServerDcatRepository repository;

	@BeforeEach
	void setUp() {
		service = new ServerDcatServiceImpl(repository);
	}

	@Test
	void when_ServerHasNoDcat_and_CreateDcat_then_ReturnIt() {
		when(repository.getServerDcat()).thenReturn(List.of());
		when(repository.saveServerDcat(any())).thenReturn(SERVER_DCAT);

		final ServerDcat createdDcat = assertDoesNotThrow(() -> service.createServerDcat(DCAT));

		assertEquals(SERVER_DCAT, createdDcat);
		InOrder inOrder = Mockito.inOrder(repository);
		inOrder.verify(repository).getServerDcat();
		inOrder.verify(repository).saveServerDcat(any());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_ServerAlreadyHasDcatConfigured_then_ThrowException() {
		when(repository.getServerDcat()).thenReturn(List.of(SERVER_DCAT));

		assertThrows(DcatAlreadyConfiguredException.class, () -> service.createServerDcat(DCAT));
		verify(repository).getServerDcat();
		verifyNoMoreInteractions(repository);
	}

	@Test
	void when_UpdateExistingDcat_then_ReturnDcat() {
		when(repository.getServerDcatById(ID)).thenReturn(Optional.of(SERVER_DCAT));
		when(repository.saveServerDcat(SERVER_DCAT)).thenReturn(SERVER_DCAT);

		ServerDcat updatedServerDcat = assertDoesNotThrow(() -> service.updateServerDcat(ID, DCAT));

		assertEquals(SERVER_DCAT, updatedServerDcat);
		InOrder inOrder = inOrder(repository);
		inOrder.verify(repository).getServerDcatById(ID);
		inOrder.verify(repository).saveServerDcat(SERVER_DCAT);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_UpdateNonExistingDcat_then_ThrowException() {
		when(repository.getServerDcatById(ID)).thenReturn(Optional.empty());
		String expectedMessage = String.format("No dcat is configured on the server with id %s", ID);

		Exception e = assertThrows(MissingServerDcatException.class, () -> service.updateServerDcat(ID, DCAT));

		assertEquals(expectedMessage, e.getMessage());
		verify(repository).getServerDcatById(ID);
		verifyNoMoreInteractions(repository);
	}

	@Test
	void when_DeleteExistingDcat_then_ReturnVoid() {
		when(repository.getServerDcatById(ID)).thenReturn(Optional.of(SERVER_DCAT));

		assertDoesNotThrow(() -> service.deleteServerDcat(ID));
		InOrder inOrder = inOrder(repository);
		inOrder.verify(repository).getServerDcatById(ID);
		inOrder.verify(repository).deleteServerDcat(ID);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_DeleteNonExistingDcat_then_ThrowException() {
		when(repository.getServerDcatById(ID)).thenReturn(Optional.empty());
		String expectedMessage = String.format("No dcat is configured on the server with id %s", ID);

		Exception e = assertThrows(MissingServerDcatException.class, () -> service.deleteServerDcat(ID));

		assertEquals(expectedMessage, e.getMessage());
		verify(repository).getServerDcatById(ID);
		verifyNoMoreInteractions(repository);
	}
}