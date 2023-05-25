package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.entities.ServerDcat;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.repositories.ServerDcatRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat.repository.ServerDcatEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat.service.ServerDcatEntityConverter;

import java.util.List;
import java.util.Optional;

public class ServerDcatMongoRepository implements ServerDcatRepository {
	private final ServerDcatEntityRepository serverDcatEntityRepository;
	private final ServerDcatEntityConverter converter = new ServerDcatEntityConverter();

	public ServerDcatMongoRepository(ServerDcatEntityRepository serverDcatEntityRepository) {
		this.serverDcatEntityRepository = serverDcatEntityRepository;
	}

	@Override
	public List<ServerDcat> findAll() {
		return serverDcatEntityRepository.findAll().stream().map(converter::toServerDcat).toList();
	}

	@Override
	public Optional<ServerDcat> findById(String id) {
		return serverDcatEntityRepository.findById(id).map(converter::toServerDcat);
	}

	@Override
	public ServerDcat save(ServerDcat serverDcat) {
		serverDcatEntityRepository.save(converter.fromServerDcat(serverDcat));
		return serverDcat;
	}

	@Override
	public void deleteById(String id) {
		serverDcatEntityRepository.deleteById(id);
	}
}
