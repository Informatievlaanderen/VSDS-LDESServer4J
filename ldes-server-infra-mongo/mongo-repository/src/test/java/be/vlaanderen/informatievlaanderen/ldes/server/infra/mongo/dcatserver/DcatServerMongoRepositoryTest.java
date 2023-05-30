package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatserver;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatserver.entity.DcatCatalogEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatserver.repository.DcatCatalogEntityRepository;
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
class DcatServerMongoRepositoryTest {
	private static final String ID = "id";
	private static final DcatServer SERVER_DCAT = new DcatServer(ID, ModelFactory.createDefaultModel());
	private DcatServerRepository dcatServerRepository;
	@Mock
	private DcatCatalogEntityRepository dcatCatalogEntityRepository;

	@BeforeEach
	void setUp() {
		dcatServerRepository = new DcatServerMongoRepository(dcatCatalogEntityRepository);
	}

	@Test
	void when_DbContainsElement_then_ReturnListWithOneServerDcat() {
		when(dcatCatalogEntityRepository.findAll()).thenReturn(List.of(new DcatCatalogEntity(ID, "")));

		List<DcatServer> retrievedDcatServers = dcatServerRepository.getServerDcat();

		assertEquals(1, retrievedDcatServers.size());
		assertEquals(SERVER_DCAT, retrievedDcatServers.get(0));
		verify(dcatCatalogEntityRepository).findAll();
	}

	@Test
	void when_DbIsEmpty_then_ReturnEmptyList() {
		when(dcatCatalogEntityRepository.findAll()).thenReturn(List.of());

		List<DcatServer> retrievedDcatServers = dcatServerRepository.getServerDcat();

		assertTrue(retrievedDcatServers.isEmpty());
		verify(dcatCatalogEntityRepository).findAll();
	}

	@Test
	void when_DbContainsElement_then_ReturnServerDcat() {
		when(dcatCatalogEntityRepository.findById(ID)).thenReturn(Optional.of(new DcatCatalogEntity(ID, "")));

		Optional<DcatServer> retrievedServerDcat = dcatServerRepository.getServerDcatById(ID);

		assertTrue(retrievedServerDcat.isPresent());
		assertEquals(SERVER_DCAT, retrievedServerDcat.get());
		verify(dcatCatalogEntityRepository).findById(ID);
	}

	@Test
	void when_DbIsEmpty_then_ReturnEmptyOptional() {
		when(dcatCatalogEntityRepository.findById(ID)).thenReturn(Optional.empty());

		Optional<DcatServer> retrieveServerDcat = dcatServerRepository.getServerDcatById(ID);

		assertTrue(retrieveServerDcat.isEmpty());
		verify(dcatCatalogEntityRepository).findById(ID);
	}

	@Test
	void when_ServerDcatIsSaved_then_ReturnIt() {
		DcatCatalogEntity dcatCatalogEntity = new DcatCatalogEntity(ID, "");
		when(dcatCatalogEntityRepository.save(any())).thenReturn(dcatCatalogEntity);

		DcatServer savedDcat = dcatServerRepository.saveServerDcat(SERVER_DCAT);

		assertEquals(SERVER_DCAT, savedDcat);
		verify(dcatCatalogEntityRepository).save(any());
	}

	@Test
	void when_DbContainsElement_then_DeleteIt() {
		dcatServerRepository.deleteServerDcat(ID);

		verify(dcatCatalogEntityRepository).deleteById(ID);
	}
}