package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.retentiontasklet;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies.RetentionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution.MemberRemover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class EventSourceRetentionTask extends RetentionTask<String> {
	private static final Logger log = LoggerFactory.getLogger(EventSourceRetentionTask.class);
	private final MemberPropertiesRepository memberPropertiesRepository;
	private final MemberRemover memberRemover;

	public EventSourceRetentionTask(RetentionPolicyCollection<String> retentionPolicyCollection, MemberPropertiesRepository memberPropertiesRepository, MemberRemover memberRemover) {
		super(retentionPolicyCollection);
		this.memberPropertiesRepository = memberPropertiesRepository;
		this.memberRemover = memberRemover;
	}

	@Override
	protected void removeMembersThatMatchRetentionPolicies(String collectionName, RetentionPolicy retentionPolicy) {
		log.atDebug().log("Start retention for event source of collection {}", collectionName);
		final Stream<MemberProperties> memberPropertiesStream = switch (retentionPolicy.getType()) {
			case TIME_BASED -> memberPropertiesRepository.retrieveExpiredMembers(collectionName,
					(TimeBasedRetentionPolicy) retentionPolicy);
			case VERSION_BASED -> memberPropertiesRepository.retrieveExpiredMembers(collectionName,
					(VersionBasedRetentionPolicy) retentionPolicy);
			case TIME_AND_VERSION_BASED -> memberPropertiesRepository.retrieveExpiredMembers(collectionName,
					(TimeAndVersionBasedRetentionPolicy) retentionPolicy);
		};

		Map<Boolean, List<MemberProperties>> areMembersRemoveableMap = memberPropertiesStream.collect(Collectors.partitioningBy(memberProperties -> !memberProperties.isInView()));
		memberRemover.deleteMembers(areMembersRemoveableMap.get(true));
		memberRemover.removeMembersFromEventSource(areMembersRemoveableMap.get(false));
		log.atDebug().log("Finished retention for event source of collection {}", collectionName);
	}
}
