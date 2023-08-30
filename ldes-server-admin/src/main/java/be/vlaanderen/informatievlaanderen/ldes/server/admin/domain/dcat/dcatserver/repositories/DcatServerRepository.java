package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;

import java.util.List;
import java.util.Optional;

public interface DcatServerRepository {
	List<DcatServer> getServerDcat();

	Optional<DcatServer> getServerDcatById(String id);

	DcatServer saveServerDcat(DcatServer dcatServer);

	void deleteServerDcat(String id);

	Optional<DcatServer> findSingleDcatServer();
}
