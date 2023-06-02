package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatserver;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatserver.repository.DcatCatalogEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatserver.service.DcatCatalogEntityConverter;

import java.util.List;
import java.util.Optional;

public class DcatServerMongoRepository implements DcatServerRepository {
	private final DcatCatalogEntityRepository dcatCatalogEntityRepository;
	private final DcatCatalogEntityConverter converter = new DcatCatalogEntityConverter();

	public DcatServerMongoRepository(DcatCatalogEntityRepository dcatCatalogEntityRepository) {
		this.dcatCatalogEntityRepository = dcatCatalogEntityRepository;
	}

	@Override
	public List<DcatServer> getServerDcat() {
		return dcatCatalogEntityRepository.findAll().stream().map(converter::toDcatServer).toList();
	}

	@Override
	public Optional<DcatServer> getServerDcatById(String id) {
		return dcatCatalogEntityRepository.findById(id).map(converter::toDcatServer);
	}

	@Override
	public DcatServer saveServerDcat(DcatServer dcatServer) {
		dcatCatalogEntityRepository.save(converter.fromDcatServer(dcatServer));
		return dcatServer;
	}

	@Override
	public void deleteServerDcat(String id) {
		dcatCatalogEntityRepository.deleteById(id);
	}

	@Override
	public Optional<DcatServer> findSingleDcatServer() {
		List<DcatServer> servers = dcatCatalogEntityRepository.findAll().stream().map(converter::toDcatServer).toList();
		if (servers.size() > 1) {
			throw new IllegalStateException("Multiple dcatServers found. There should be 0 or 1 record of this.");
		}

		return servers.isEmpty() ? Optional.empty() : Optional.of(servers.get(0));
	}

}
