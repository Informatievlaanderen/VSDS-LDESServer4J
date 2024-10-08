package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.DeletionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.PageMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.RetentionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.RETENTION_CRON_KEY;

@Service
@EnableScheduling
public class RetentionService {

	private static final Logger log = LoggerFactory.getLogger(RetentionService.class);

	private final MemberPropertiesRepository memberPropertiesRepository;
	private final PageMemberRepository pageMemberRepository;
	private final MemberRemover memberRemover;
	private final RetentionPolicyCollection retentionPolicyCollection;
	private final DeletionPolicyCollection deletionPolicyCollection;

	public RetentionService(MemberPropertiesRepository memberPropertiesRepository, PageMemberRepository pageMemberRepository, MemberRemover memberRemover,
	                        RetentionPolicyCollection retentionPolicyCollection, DeletionPolicyCollection deletionPolicyCollection) {
		this.memberPropertiesRepository = memberPropertiesRepository;
		this.pageMemberRepository = pageMemberRepository;
		this.memberRemover = memberRemover;
		this.retentionPolicyCollection = retentionPolicyCollection;
        this.deletionPolicyCollection = deletionPolicyCollection;
    }

	@SuppressWarnings("java:S6857")
	@Scheduled(cron = RETENTION_CRON_KEY)
	public void executeRetentionPolicies() {
		if(retentionPolicyCollection.isEmpty() && deletionPolicyCollection.isEmpty()) {
			log.atDebug().log("Retention skipped: no retention policies found");
			return;
		}
		log.atDebug().log("Start retention");
		retentionPolicyCollection
                .getRetentionPolicyMap()
                .forEach(this::removeMembersFromViewThatMatchRetentionPolicies);
		deletionPolicyCollection
				.getEventSourceRetentionPolicyMap()
				.forEach(this::removeMembersFromEventSourceThatMatchRetentionPolicies);
		log.atDebug().log("Finish retention");
	}

	private void removeMembersFromViewThatMatchRetentionPolicies(ViewName viewName,
																 RetentionPolicy retentionPolicy) {
		List<Long> expiredMemberIds = switch (retentionPolicy.getType()) {
			case TIME_BASED -> memberPropertiesRepository.findExpiredMembers(viewName,
					(TimeBasedRetentionPolicy) retentionPolicy);
			case VERSION_BASED -> memberPropertiesRepository.findExpiredMembers(viewName,
					(VersionBasedRetentionPolicy) retentionPolicy);
			case TIME_AND_VERSION_BASED -> memberPropertiesRepository.findExpiredMembers(viewName,
					(TimeAndVersionBasedRetentionPolicy) retentionPolicy);
		};
		pageMemberRepository.deleteByViewNameAndMembersIds(viewName, expiredMemberIds);
	}

	private void removeMembersFromEventSourceThatMatchRetentionPolicies(String collectionName,
														 RetentionPolicy retentionPolicy) {
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
	}
}