package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.RetentionConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimebasedRetentionProperties.DURATION;

public class RetentionPolicyCreatorImpl implements RetentionPolicyCreator {

	public static final String TIMEBASED_RETENTION_POLICY = "timebased";

	@Override
	public List<RetentionPolicy> createRetentionPolicyListForView(ViewSpecification viewSpecification) {
		return viewSpecification
				.getRetentionConfigs()
				.stream()
				.map(this::getRetentionPolicy)
				.toList();
	}

	private RetentionPolicy getRetentionPolicy(RetentionConfig retentionConfig) {
		// TODO update so that multiple retention policies can easily be incorporated
		if (TIMEBASED_RETENTION_POLICY.equals(retentionConfig.getName())) {
			return new TimeBasedRetentionPolicy(
					retentionConfig.getProperties().get(DURATION));
		}
		throw new IllegalArgumentException("Invalid retention Policy: " + retentionConfig.getName());
	}

}
