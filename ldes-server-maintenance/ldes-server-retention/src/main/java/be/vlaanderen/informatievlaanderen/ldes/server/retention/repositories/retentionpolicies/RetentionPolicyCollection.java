package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies;

import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.services.RetentionPolicyEmptinessChecker;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

import java.util.Map;

public interface RetentionPolicyCollection<K> extends RetentionPolicyEmptinessChecker {
	Map<K, RetentionPolicy> getRetentionPolicies();
}
