package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.services.DcatDatasetService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.exceptions.MissingDcatServerException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DcatAlreadyConfiguredException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.DcatShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service.DcatViewService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.HOST_NAME_KEY;

@Service
public class DcatServerServiceImpl implements DcatServerService {

	private final DcatServerRepository dcatServerRepository;
	private final DcatViewService dcatViewService;
	private final DcatDatasetService dcatDatasetService;
	private final String hostName;

	private final DcatShaclValidator dcatShaclValidator;

	public DcatServerServiceImpl(DcatServerRepository dcatServerRepository,
			DcatViewService dcatViewService,
			DcatDatasetService dcatDatasetService,
			@Value(HOST_NAME_KEY) String hostName,
			DcatShaclValidator dcatShaclValidator) {
		this.dcatServerRepository = dcatServerRepository;
		this.dcatViewService = dcatViewService;
		this.dcatDatasetService = dcatDatasetService;
		this.hostName = hostName;
		this.dcatShaclValidator = dcatShaclValidator;
	}

	@Override
	public Model getComposedDcat() {
		final Model composedDcat = ModelFactory.createDefaultModel();

		dcatServerRepository
				.findSingleDcatServer()
				.ifPresent(dcatServer -> composedDcat.add(getStatementsForComposedDcat(dcatServer)));

		dcatShaclValidator.validate(composedDcat, null);
		return composedDcat;
	}

	private List<Statement> getStatementsForComposedDcat(DcatServer dcatServer) {
		final List<Statement> statements = new ArrayList<>();

		final List<DcatView> views = dcatViewService.findAll();
		statements.addAll(
				views.stream().flatMap(dcatView -> dcatView.getStatementsWithBase(hostName).stream()).toList());

		final List<DcatDataset> datasets = dcatDatasetService.findAll();
		statements.addAll(
				datasets.stream().map(dataset -> dataset.getModelWithIdentity(hostName))
						.flatMap(model -> model.listStatements().toList().stream()).toList());

		statements.addAll(dcatServer.getStatementsWithBase(hostName, views, datasets));

		return statements;
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
