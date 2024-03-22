package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface MemberPropertiesRepository {

	void insertAll(List<MemberProperties> memberProperties);

	Optional<MemberProperties> retrieve(String id);

	void addViewToAll(ViewName viewName);

	List<MemberProperties> getMemberPropertiesOfVersionAndView(String versionOf, String viewName);

	Stream<MemberProperties> getMemberPropertiesWithViewReference(ViewName viewName);

	void removeViewReference(String id, String viewName);

	void removeMemberPropertiesOfCollection(String collectionName);

	void deleteById(String id);

	/**
	 * Finds all
	 *
	 * @param viewName
	 * @param policy
	 */
	Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName, TimeBasedRetentionPolicy policy);
	Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName, VersionBasedRetentionPolicy policy);
	Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName, TimeAndVersionBasedRetentionPolicy policy);
}
