package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.entities.ServerDcat;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.repositories.ServerDcatRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat.entity.ServerDcatEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat.repository.ServerDcatEntityRepository;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServerDcatMongoRepositoryTest {
	private static final String ID = "id";
	private static final ServerDcat SERVER_DCAT = new ServerDcat(ID, ModelFactory.createDefaultModel());
	private ServerDcatRepository serverDcatRepository;
	@Mock
	private ServerDcatEntityRepository serverDcatEntityRepository;

	@BeforeEach
	void setUp() {
		serverDcatRepository = new ServerDcatMongoRepository(serverDcatEntityRepository);
	}

	@Test
	void when_DbContainsElement_then_ReturnListWithOneServerDcat() {
		when(serverDcatEntityRepository.findAll()).thenReturn(List.of(new ServerDcatEntity(ID, "")));

		List<ServerDcat> retrievedServerDcats = serverDcatRepository.getServerDcat();

		assertEquals(1, retrievedServerDcats.size());
		assertEquals(SERVER_DCAT, retrievedServerDcats.get(0));
		verify(serverDcatEntityRepository).findAll();
	}

	@Test
	void when_DbIsEmpty_then_ReturnEmptyList() {
		when(serverDcatEntityRepository.findAll()).thenReturn(List.of());

		List<ServerDcat> retrievedServerDcats = serverDcatRepository.getServerDcat();

		assertTrue(retrievedServerDcats.isEmpty());
		verify(serverDcatEntityRepository).findAll();
	}

	@Test
	void when_DbContainsElement_then_ReturnServerDcat() {
		when(serverDcatEntityRepository.findById(ID)).thenReturn(Optional.of(new ServerDcatEntity(ID, "")));

		Optional<ServerDcat> retrievedServerDcat = serverDcatRepository.getServerDcatById(ID);

		assertTrue(retrievedServerDcat.isPresent());
		assertEquals(SERVER_DCAT, retrievedServerDcat.get());
		verify(serverDcatEntityRepository).findById(ID);
	}

	@Test
	void when_DbIsEmpty_then_ReturnEmptyOptional() {
		when(serverDcatEntityRepository.findById(ID)).thenReturn(Optional.empty());

		Optional<ServerDcat> retrieveServerDcat = serverDcatRepository.getServerDcatById(ID);

		assertTrue(retrieveServerDcat.isEmpty());
		verify(serverDcatEntityRepository).findById(ID);
	}

	@Test
	void when_ServerDcatIsSaved_then_ReturnIt() {
		ServerDcatEntity serverDcatEntity = new ServerDcatEntity(ID, "");
		when(serverDcatEntityRepository.save(any())).thenReturn(serverDcatEntity);

		ServerDcat savedDcat = serverDcatRepository.saveServerDcat(SERVER_DCAT);

		assertEquals(SERVER_DCAT, savedDcat);
		verify(serverDcatEntityRepository).save(any());
	}

	@Test
	void when_DbContainsElement_then_DeleteIt() {
		serverDcatRepository.deleteServerDcat(ID);

		verify(serverDcatEntityRepository).deleteById(ID);
	}
}