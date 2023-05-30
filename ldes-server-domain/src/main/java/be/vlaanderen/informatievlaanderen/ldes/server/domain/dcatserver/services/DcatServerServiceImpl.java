package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.exceptions.MissingDcatServerException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DcatAlreadyConfiguredException;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DcatServerServiceImpl implements DcatServerService {
	private final DcatServerRepository dcatServerRepository;

	public DcatServerServiceImpl(DcatServerRepository dcatServerRepository) {
		this.dcatServerRepository = dcatServerRepository;
	}

	@Override
	public DcatServer createDcatServer(Model dcat) {
		List<DcatServer> dcatServers = dcatServerRepository.getServerDcat();
		if (!dcatServers.isEmpty()) {
			throw new DcatAlreadyConfiguredException(dcatServers.get(0).getId());
		}
		final DcatServer dcatServer = new DcatServer(UUID.randomUUID().toString(), dcat);
		return dcatServerRepository.saveServerDcat(dcatServer);
	}

	@Override
	public DcatServer updateDcatServer(String id, Model dcat) {
		if (dcatServerRepository.getServerDcatById(id).isEmpty()) {
			throw new MissingDcatServerException(id);
		}
		final DcatServer dcatServer = new DcatServer(id, dcat);
		return dcatServerRepository.saveServerDcat(dcatServer);
	}

	@Override
	public void deleteDcatServer(String id) {
		dcatServerRepository.deleteServerDcat(id);
	}
}
