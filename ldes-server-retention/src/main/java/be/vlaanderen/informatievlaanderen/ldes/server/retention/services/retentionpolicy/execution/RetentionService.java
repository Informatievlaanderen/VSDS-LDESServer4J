package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.RetentionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.RETENTION_INTERVAL_KEY;

@Service
public class RetentionService {

	private final MemberPropertiesRepository memberPropertiesRepository;
	private final MemberRemover memberRemover;
	private final RetentionPolicyCollection retentionPolicyCollection;

	public RetentionService(MemberPropertiesRepository memberPropertiesRepository, MemberRemover memberRemover,
			RetentionPolicyCollection retentionPolicyCollection) {
		this.memberPropertiesRepository = memberPropertiesRepository;
		this.memberRemover = memberRemover;
		this.retentionPolicyCollection = retentionPolicyCollection;
	}

	@Scheduled(fixedDelayString = RETENTION_INTERVAL_KEY)
	public void executeRetentionPolicies() {
		retentionPolicyCollection
				.getRetentionPolicyMap()
				.entrySet()
				.stream()
				.filter(this::viewHasRetentionPolicies)
				.forEach(viewWithRetentionPolicies -> removeMembersFromViewThatMatchRetentionPolicies(
						viewWithRetentionPolicies.getKey(), viewWithRetentionPolicies.getValue()));
	}

	private boolean viewHasRetentionPolicies(Map.Entry<String, List<RetentionPolicy>> entry) {
		return !entry.getValue().isEmpty();
	}

	private void removeMembersFromViewThatMatchRetentionPolicies(String viewName,
			List<RetentionPolicy> retentionPoliciesOfView) {
		memberPropertiesRepository.getMemberPropertiesWithViewReference(viewName)
				.filter(memberProperties -> memberMatchesAllRetentionPoliciesOfView(retentionPoliciesOfView, viewName,
						memberProperties))
				.forEach(memberProperties -> memberRemover.removeMemberFromView(memberProperties, viewName));
	}

	private boolean memberMatchesAllRetentionPoliciesOfView(List<RetentionPolicy> retentionPolicies,
			String viewName, MemberProperties memberProperties) {
		return retentionPolicies
				.stream()
				.allMatch(retentionPolicy -> retentionPolicy.matchesPolicyOfView(memberProperties, viewName));
	}

}