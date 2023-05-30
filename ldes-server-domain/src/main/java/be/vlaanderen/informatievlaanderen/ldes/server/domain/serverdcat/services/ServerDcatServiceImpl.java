package be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DcatAlreadyConfiguredException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingServerDcatException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.entities.ServerDcat;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.repositories.ServerDcatRepository;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ServerDcatServiceImpl implements ServerDcatService {
	private final ServerDcatRepository serverDcatRepository;

	public ServerDcatServiceImpl(ServerDcatRepository serverDcatRepository) {
		this.serverDcatRepository = serverDcatRepository;
	}

	@Override
	public ServerDcat createServerDcat(Model dcat) {
		List<ServerDcat> serverDcats = serverDcatRepository.getServerDcat();
		if (!serverDcats.isEmpty()) {
			throw new DcatAlreadyConfiguredException(serverDcats.get(0).getId());
		}
		final ServerDcat serverDcat = new ServerDcat(UUID.randomUUID().toString(), dcat);
		return serverDcatRepository.saveServerDcat(serverDcat);
	}

	@Override
	public ServerDcat updateServerDcat(String id, Model dcat) {
		if (serverDcatRepository.getServerDcatById(id).isEmpty()) {
			throw new MissingServerDcatException(id);
		}
		final ServerDcat serverDcat = new ServerDcat(id, dcat);
		return serverDcatRepository.saveServerDcat(serverDcat);
	}

	@Override
	public void deleteServerDcat(String id) {
		serverDcatRepository.deleteServerDcat(id);
	}
}
