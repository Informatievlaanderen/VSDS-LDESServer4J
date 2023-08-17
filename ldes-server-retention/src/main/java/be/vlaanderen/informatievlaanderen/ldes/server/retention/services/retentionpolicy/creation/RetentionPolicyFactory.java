package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

import java.util.List;

public interface RetentionPolicyFactory {
	List<RetentionPolicy> getRetentionPolicyListForView(ViewSpecification viewSpecification);
}
