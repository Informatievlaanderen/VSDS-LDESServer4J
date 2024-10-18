package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.retentiontasklet;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.repository.PageMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViewRetentionTask extends RetentionTask {
	private static final Logger log = LoggerFactory.getLogger(ViewRetentionTask.class);
	private final MemberPropertiesRepository memberPropertiesRepository;
	private final PageMemberRepository pageMemberRepository;

	public ViewRetentionTask(MemberPropertiesRepository memberPropertiesRepository, PageMemberRepository pageMemberRepository) {
		this.memberPropertiesRepository = memberPropertiesRepository;
		this.pageMemberRepository = pageMemberRepository;
	}

	@Override
	protected void removeMembersThatMatchRetentionPolicies(String name, RetentionPolicy retentionPolicy) {
		final ViewName viewName = ViewName.fromString(name);
		log.atDebug().log("Start retention for view: {}", name);
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
