package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface MemberPropertiesRepository {

	void save(MemberProperties memberProperties);

	Optional<MemberProperties> retrieve(String id);

	void addViewReference(String id, String viewName);

	List<MemberProperties> getMemberPropertiesOfVersion(String versionOf);

	Stream<MemberProperties> getMemberPropertiesWithViewReference(String viewName);

	void removeViewReference(String id, String viewName);

	void removeMemberPropertiesOfCollection(String collectionName);

	void deleteById(String id);
}
