package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;

import java.util.List;

public interface RetentionPolicyFactory {
	List<RetentionPolicy> getRetentionPolicyListForView(ViewSpecification viewSpecification);
}
