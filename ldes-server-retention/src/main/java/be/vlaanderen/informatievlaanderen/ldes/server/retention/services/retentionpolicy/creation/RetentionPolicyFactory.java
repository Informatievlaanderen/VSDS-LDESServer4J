package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

import java.util.List;
import java.util.Optional;

public interface RetentionPolicyFactory {
    Optional<RetentionPolicy> extractRetentionPolicy(ViewSpecification viewSpecification);
}
