package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

import java.util.List;

public interface RetentionPolicyFactory {
	List<RetentionPolicy> getRetentionPolicyListForView(ViewSpecification viewSpecification);
}
