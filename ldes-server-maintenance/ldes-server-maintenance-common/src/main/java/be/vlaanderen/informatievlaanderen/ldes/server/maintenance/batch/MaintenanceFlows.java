package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.valueobjects.DecidedFlowExecutionStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MaintenanceFlows {
	public static final String MAINTENANCE_JOB = "maintenance";

	@Bean
	public FlowJobBuilder maintenanceJobBuilder(JobRepository jobRepository,
	                                            Flow viewRetentionFlow,
	                                            Flow eventSourceRetentionFlow,
	                                            Step completedJobExecutionsStep) {
		return new JobBuilder(MAINTENANCE_JOB, jobRepository)
				.start(viewRetentionFlow)
				.next(eventSourceRetentionFlow)
				.next(completedJobExecutionsStep)
				.end();
	}

	@Bean
	public Flow viewRetentionFlow(@Qualifier("viewRetentionStep") Step viewRetentionStep,
	                              @Qualifier("compactionStep") Step compactionStep,
	                              @Qualifier("deletionStep") Step deletionStep,
	                              @Qualifier("viewRetentionExecutionDecider") JobExecutionDecider decider) {
		return new FlowBuilder<Flow>("viewRetentionFlow")
				.start(decider).on(DecidedFlowExecutionStatus.SKIP.pattern()).end()
				.from(decider).on(DecidedFlowExecutionStatus.CONTINUE.pattern()).to(viewRetentionStep)
				.from(viewRetentionStep).on("*").to(compactionStep)
				.from(compactionStep).on("*").to(deletionStep)
				.end();
	}

	@Bean
	public Flow eventSourceRetentionFlow(@Qualifier("eventSourceRetentionStep") Step step,
	                                     @Qualifier("eventSourceRetentionExecutionDecider") JobExecutionDecider decider) {
		return new FlowBuilder<Flow>("eventSourceRetentionFlow")
				.start(decider).on(DecidedFlowExecutionStatus.SKIP.pattern()).end()
				.from(decider).on(DecidedFlowExecutionStatus.CONTINUE.pattern()).to(step)
				.end();
	}
}
