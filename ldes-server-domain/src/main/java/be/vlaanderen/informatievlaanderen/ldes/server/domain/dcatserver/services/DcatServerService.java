package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.entities.DcatServer;
import org.apache.jena.rdf.model.Model;

public interface DcatServerService {

	Model getComposedDcat();

	DcatServer createDcatServer(Model dcat);

	DcatServer updateDcatServer(String id, Model dcat);

	void deleteDcatServer(String id);

}
