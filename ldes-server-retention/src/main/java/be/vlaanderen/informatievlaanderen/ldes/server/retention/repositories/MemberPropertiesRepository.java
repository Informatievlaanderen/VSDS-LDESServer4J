package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;

import java.util.Optional;

public interface MemberPropertiesRepository {

	void save(MemberProperties memberProperties);

	Optional<MemberProperties> retrieve(String id);

	void addViewReference(String id, String viewName);
}
