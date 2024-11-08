package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.retentiontasklet;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public abstract class RetentionTask implements Tasklet {

	@Override
	public final RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		final ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
		if(executionContext.get("retentionPolicy") instanceof RetentionPolicy retentionPolicy) {
			final String name = executionContext.getString("name");
			removeMembersThatMatchRetentionPolicies(name, retentionPolicy);
		}
		return RepeatStatus.FINISHED;
	}

	protected abstract void removeMembersThatMatchRetentionPolicies(String name, RetentionPolicy retentionPolicy);
}
