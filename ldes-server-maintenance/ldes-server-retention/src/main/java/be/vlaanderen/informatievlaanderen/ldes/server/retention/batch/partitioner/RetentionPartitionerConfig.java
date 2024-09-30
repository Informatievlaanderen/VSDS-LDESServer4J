package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.partitioner;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.EventSourceRetentionPolicyProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.ViewRetentionPolicyProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies.RetentionPolicyCollection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetentionPartitionerConfig {
	@Bean
	public RetentionPolicyPartitioner viewRetentionPolicyPartitioner(RetentionPolicyCollection<ViewRetentionPolicyProvider> viewRetentionPolicyCollection) {
		return new RetentionPolicyPartitioner(viewRetentionPolicyCollection);
	}

	@Bean
	public RetentionPolicyPartitioner eventSourceRetentionPolicyPartitioner(RetentionPolicyCollection<EventSourceRetentionPolicyProvider> eventSourceRetentionPolicyCollection) {
		return new RetentionPolicyPartitioner(eventSourceRetentionPolicyCollection);
	}
}
