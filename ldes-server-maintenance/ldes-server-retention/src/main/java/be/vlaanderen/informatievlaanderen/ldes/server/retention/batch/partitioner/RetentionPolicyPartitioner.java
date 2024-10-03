package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.partitioner;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.RetentionPolicyProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies.RetentionPolicyCollection;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.Map;
import java.util.stream.Collectors;

public class RetentionPolicyPartitioner implements Partitioner {
	private final RetentionPolicyCollection<? extends RetentionPolicyProvider> retentionPolicyCollection;

	public RetentionPolicyPartitioner(RetentionPolicyCollection<? extends RetentionPolicyProvider> retentionPolicyCollection) {
		this.retentionPolicyCollection = retentionPolicyCollection;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		return retentionPolicyCollection.getRetentionPolicies().stream().collect(
				Collectors.toMap(
						retentionPolicy -> "retention:%s".formatted(retentionPolicy.getName()),
						retentionPolicy -> new ExecutionContext(Map.of(
								"name", retentionPolicy.getName(),
								"retentionPolicy", retentionPolicy.retentionPolicy()
						))
				)
		);
	}
}
