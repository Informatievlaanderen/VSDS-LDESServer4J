package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies;

import java.util.Set;

public interface RetentionPolicyCollection<T> {
	Set<T> getRetentionPolicies();
}
