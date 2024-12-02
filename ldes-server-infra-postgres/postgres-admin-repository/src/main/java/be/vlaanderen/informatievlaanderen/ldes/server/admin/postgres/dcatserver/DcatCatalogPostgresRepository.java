package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.repository.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.repository.DcatCatalogEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.service.DcatCatalogEntityConverter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class DcatCatalogPostgresRepository implements DcatServerRepository {
	private final DcatCatalogEntityRepository repository;
	private final DcatCatalogEntityConverter converter = new DcatCatalogEntityConverter();

	public DcatCatalogPostgresRepository(DcatCatalogEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<DcatServer> getServerDcat() {
		return repository.findAll().stream().map(converter::toDcatServer).toList();
	}

	@Override
	public Optional<DcatServer> getServerDcatById(String id) {
		return repository.findById(id).map(converter::toDcatServer);
	}

	@Override
	@Transactional
	public DcatServer saveServerDcat(DcatServer dcatServer) {
		repository.save(converter.fromDcatServer(dcatServer));
		return dcatServer;
	}

	@Override
	public void deleteServerDcat(String id) {
		repository.deleteById(id);
	}

	@Override
	public Optional<DcatServer> findSingleDcatServer() {
		return repository.findAll()
				.stream()
				.map(converter::toDcatServer)
				.findFirst();
	}
}
