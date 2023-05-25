package be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.entities.ServerDcat;

import java.util.List;
import java.util.Optional;

public interface ServerDcatRepository {
	List<ServerDcat> findAll();

	Optional<ServerDcat> findById(String id);

	ServerDcat save(ServerDcat serverDcat);

	void deleteById(String id);
}
