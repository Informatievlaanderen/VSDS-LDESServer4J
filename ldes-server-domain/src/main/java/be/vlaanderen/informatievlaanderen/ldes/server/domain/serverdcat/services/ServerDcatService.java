package be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.entities.ServerDcat;
import org.apache.jena.rdf.model.Model;

public interface ServerDcatService {
	ServerDcat createServerDcat(Model dcat);

	ServerDcat updateServerDcat(String id, Model dcat);

	void deleteServerDcat(String id);
}
