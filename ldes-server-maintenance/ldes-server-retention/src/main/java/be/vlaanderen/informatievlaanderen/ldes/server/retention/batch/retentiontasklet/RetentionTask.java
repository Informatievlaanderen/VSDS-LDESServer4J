package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.retentiontasklet;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies.RetentionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public abstract class RetentionTask<K> implements Tasklet {
	private static final Logger log = LoggerFactory.getLogger(RetentionTask.class);
	private final RetentionPolicyCollection<K> retentionPolicyCollection;

	protected RetentionTask(RetentionPolicyCollection<K> retentionPolicyCollection) {
		this.retentionPolicyCollection = retentionPolicyCollection;
	}

	@Override
	public final RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		if(retentionPolicyCollection.isEmpty()) {
			log.atDebug().log("Retention skipped: no retention policies found");
			return RepeatStatus.FINISHED;
		}
		retentionPolicyCollection
				.getRetentionPolicies()
				.forEach(this::removeMembersThatMatchRetentionPolicies);
		return RepeatStatus.FINISHED;
	}


	protected abstract void removeMembersThatMatchRetentionPolicies(K key, RetentionPolicy retentionPolicy);
}
