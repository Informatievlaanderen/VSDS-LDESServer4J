package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies;

import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.services.RetentionPolicyEmptinessChecker;

import java.util.Set;

public interface RetentionPolicyCollection<T> extends RetentionPolicyEmptinessChecker {
	Set<T> getRetentionPolicies();
}
