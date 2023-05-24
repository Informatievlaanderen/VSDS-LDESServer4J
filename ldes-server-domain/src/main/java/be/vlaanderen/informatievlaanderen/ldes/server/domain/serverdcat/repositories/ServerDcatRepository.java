package be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.entities.ServerDcat;

import java.util.List;
import java.util.Optional;

public interface ServerDcatRepository {
	List<ServerDcat> getServerDcat();

	Optional<ServerDcat> getServerDcatById(String id);

	ServerDcat saveServerDcat(ServerDcat serverDcat);

	void deleteServerDcat(String id);
}
