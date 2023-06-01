package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.exceptions.MissingDcatServerException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DcatAlreadyConfiguredException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.DcatShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.DcatViewService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DcatServerServiceImpl implements DcatServerService {

	private final DcatServerRepository dcatServerRepository;
	private final DcatViewService dcatViewService;
	private final String hostName;

	private final DcatShaclValidator dcatShaclValidator;

	public DcatServerServiceImpl(DcatServerRepository dcatServerRepository,
			DcatViewService dcatViewService,
			@Value("${ldes-server.host-name}") String hostName, DcatShaclValidator dcatShaclValidator) {
		this.dcatServerRepository = dcatServerRepository;
		this.dcatViewService = dcatViewService;
		this.hostName = hostName;
		this.dcatShaclValidator = dcatShaclValidator;
	}

	// TODO TVB: 31/05/2023 test
	@Override
	public Model getComposedDcat() {
		Model composedDcat = ModelFactory.createDefaultModel();

		dcatServerRepository.findSingleDcatServer().ifPresent(dcatServer -> {
			List<DcatView> views = dcatViewService.findAll();
			composedDcat.add(
					views.stream().flatMap(dcatView -> dcatView.getStatementsWithBase(hostName).stream()).toList());

			// TODO TVPJ: 31/05/2023 add PJ datasets in line with views

			composedDcat.add(dcatServer.getStatementsWithBase(hostName, views));
		});

		dcatShaclValidator.validate(composedDcat, null);
		return composedDcat;
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
