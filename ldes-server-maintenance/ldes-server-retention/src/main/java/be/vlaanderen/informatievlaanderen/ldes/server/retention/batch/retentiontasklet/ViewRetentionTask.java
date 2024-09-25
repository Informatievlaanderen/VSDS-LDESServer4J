package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.retentiontasklet;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.PageMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies.RetentionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViewRetentionTask extends RetentionTask<ViewName> {
	private static final Logger log = LoggerFactory.getLogger(ViewRetentionTask.class);
	private final MemberPropertiesRepository memberPropertiesRepository;
	private final PageMemberRepository pageMemberRepository;

	public ViewRetentionTask(RetentionPolicyCollection<ViewName> retentionPolicyCollection, MemberPropertiesRepository memberPropertiesRepository, PageMemberRepository pageMemberRepository) {
		super(retentionPolicyCollection);
		this.memberPropertiesRepository = memberPropertiesRepository;
		this.pageMemberRepository = pageMemberRepository;
	}

	@Override
	protected void removeMembersThatMatchRetentionPolicies(ViewName viewName, RetentionPolicy retentionPolicy) {
		log.atDebug().log("Start retention for view: {}", viewName.asString());
		List<Long> expiredMemberIds = switch (retentionPolicy.getType()) {
			case TIME_BASED -> memberPropertiesRepository.findExpiredMembers(viewName,
					(TimeBasedRetentionPolicy) retentionPolicy);
			case VERSION_BASED -> memberPropertiesRepository.findExpiredMembers(viewName,
					(VersionBasedRetentionPolicy) retentionPolicy);
			case TIME_AND_VERSION_BASED -> memberPropertiesRepository.findExpiredMembers(viewName,
					(TimeAndVersionBasedRetentionPolicy) retentionPolicy);
		};
		pageMemberRepository.deleteByViewNameAndMembersIds(viewName, expiredMemberIds);
		log.atDebug().log("Finished retention for view: {}", viewName.asString());
	}
}
