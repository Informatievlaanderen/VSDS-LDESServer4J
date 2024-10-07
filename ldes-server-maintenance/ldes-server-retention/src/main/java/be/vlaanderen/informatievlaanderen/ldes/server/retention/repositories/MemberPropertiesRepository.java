package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;

import java.util.List;
import java.util.stream.Stream;

public interface MemberPropertiesRepository {

	void deleteAllByIds(List<Long> id);

	void removeFromEventSource(List<Long> id);

	/**
	 * Finds all
	 *
	 * @param viewName
	 * @param policy
	 */
	List<Long> findExpiredMembers(ViewName viewName, TimeBasedRetentionPolicy policy);
	List<Long> findExpiredMembers(ViewName viewName, VersionBasedRetentionPolicy policy);
	List<Long> findExpiredMembers(ViewName viewName, TimeAndVersionBasedRetentionPolicy policy);
	Stream<MemberProperties> retrieveExpiredMembers(String collectionName, TimeBasedRetentionPolicy policy);
	Stream<MemberProperties> retrieveExpiredMembers(String collectionName, VersionBasedRetentionPolicy policy);
	Stream<MemberProperties> retrieveExpiredMembers(String collectionName, TimeAndVersionBasedRetentionPolicy policy);
}
