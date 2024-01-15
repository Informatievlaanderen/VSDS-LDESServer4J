package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.spi.RetentionPolicyEmptinessChecker;

import java.util.Map;

public interface RetentionPolicyCollection extends RetentionPolicyEmptinessChecker {

	Map<ViewName, RetentionPolicy> getRetentionPolicyMap();
}
