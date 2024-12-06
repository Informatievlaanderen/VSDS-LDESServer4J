package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBatchTest
@SpringBootTest(classes = {MaintenanceFlows.class})
@EnableAutoConfiguration
@Import(MaintenanceJobDefinitionTest.TestSteps.class)
class MaintenanceJobDefinitionTest {
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	@Autowired
	private FlowJobBuilder maintenanceJobBuilder;
	@SpyBean(name = "viewRetentionStep")
	private Step viewRetentionStep;
	@SpyBean(name = "compactionStep")
	private Step compactionStep;
	@SpyBean(name = "deletionStep")
	private Step deletionStep;
	@SpyBean(name = "eventSourceRetentionStep")
	private Step eventSourceRetentionStep;
	@SpyBean(name = "completedJobExecutionsStep")
	private Step completedJobExecutionsStep;

	@MockBean(name = "viewRetentionExecutionDecider")
	private JobExecutionDecider viewRetentionExecutionDecider;
	@MockBean(name = "eventSourceRetentionExecutionDecider")
	private JobExecutionDecider eventSourceRetentionExecutionDecider;

	@BeforeEach
	void setUp() {
		jobLauncherTestUtils.setJob(maintenanceJobBuilder.build());
	}

	@Test
	void given_EventSourceRetentionShouldBeSkipped_when_Execute_then_VerifyNoEventSourceRetentionStepInteraction() throws Exception {
		when(viewRetentionExecutionDecider.decide(any(), any())).thenReturn(new FlowExecutionStatus("CONTINUE"));
		when(eventSourceRetentionExecutionDecider.decide(any(), any())).thenReturn(new FlowExecutionStatus("SKIP"));
		final JobParameters jobParameters = new JobParametersBuilder()
				.addLocalDateTime("triggered", LocalDateTime.now())
				.toJobParameters();

		final JobExecution result = jobLauncherTestUtils.launchJob(jobParameters);

		assertThat(result.getExitStatus().getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
		verify(viewRetentionStep).execute(any());
		verify(compactionStep).execute(any());
		verify(deletionStep).execute(any());
		verify(eventSourceRetentionStep, never()).execute(any());
	}

	@Test
	void given_NothingShouldBeSkipped_when_Execute_then_VerifyAllStepsInteraction() throws Exception {
		when(viewRetentionExecutionDecider.decide(any(), any())).thenReturn(new FlowExecutionStatus("CONTINUE"));
		when(eventSourceRetentionExecutionDecider.decide(any(), any())).thenReturn(new FlowExecutionStatus("CONTINUE"));
		final JobParameters jobParameters = new JobParametersBuilder()
				.addLocalDateTime("triggered", LocalDateTime.now())
				.toJobParameters();

		final JobExecution result = jobLauncherTestUtils.launchJob(jobParameters);

		assertThat(result.getExitStatus().getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
		verify(viewRetentionStep).execute(any());
		verify(compactionStep).execute(any());
		verify(deletionStep).execute(any());
		verify(eventSourceRetentionStep).execute(any());
	}

	@Test
	void given_AllShouldBeSkipped_when_Execute_then_VerifyNoStepsInteraction() throws Exception {
		when(viewRetentionExecutionDecider.decide(any(), any())).thenReturn(new FlowExecutionStatus("SKIP"));
		when(eventSourceRetentionExecutionDecider.decide(any(), any())).thenReturn(new FlowExecutionStatus("SKIP"));
		doNothing().when(viewRetentionStep).execute(any());
		final JobParameters jobParameters = new JobParametersBuilder()
				.addLocalDateTime("triggered", LocalDateTime.now())
				.toJobParameters();

		final JobExecution result = jobLauncherTestUtils.launchJob(jobParameters);

		assertThat(result.getExitStatus().getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
		verify(viewRetentionStep, never()).execute(any());
		verify(compactionStep, never()).execute(any());
		verify(deletionStep, never()).execute(any());
		verify(eventSourceRetentionStep, never()).execute(any());
		verify(completedJobExecutionsStep).execute(any());
	}

	@Test
	void given_ViewRetentionShouldBeSkipped_when_Execute_then_VerifyOnlyEventSourceRetentionStepInteraction() throws Exception {
		when(viewRetentionExecutionDecider.decide(any(), any())).thenReturn(new FlowExecutionStatus("SKIP"));
		when(eventSourceRetentionExecutionDecider.decide(any(), any())).thenReturn(new FlowExecutionStatus("CONTINUE"));
		final JobParameters jobParameters = new JobParametersBuilder()
				.addLocalDateTime("triggered", LocalDateTime.now())
				.toJobParameters();

		final JobExecution result = jobLauncherTestUtils.launchJob(jobParameters);

		assertThat(result.getExitStatus().getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
		verify(viewRetentionStep, never()).execute(any());
		verify(compactionStep, never()).execute(any());
		verify(deletionStep, never()).execute(any());
		verify(eventSourceRetentionStep).execute(any());
	}

	@TestConfiguration
	static class TestSteps {
		private final Tasklet tasklet = (contribution, chunkContext) -> RepeatStatus.FINISHED;

		@Bean(name = "viewRetentionStep")
		public Step viewRetentionStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
			return new StepBuilder("viewRetentionStep", jobRepository)
					.tasklet(tasklet, transactionManager)
					.build();
		}

		@Bean(name = "compactionStep")
		public Step compactionStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
			return new StepBuilder("compactionStep", jobRepository)
					.tasklet(tasklet, transactionManager)
					.build();
		}

		@Bean(name = "deletionStep")
		public Step deletionStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
			return new StepBuilder("deletionStep", jobRepository)
					.tasklet(tasklet, transactionManager)
					.build();
		}

		@Bean(name = "eventSourceRetentionStep")
		public Step eventSourceRetentionStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
			return new StepBuilder("eventSourceRetentionStep", jobRepository)
					.tasklet(tasklet, transactionManager)
					.build();
		}

		@Bean(name = "completedJobExecutionsStep")
		public Step completedJobExecutionsStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
			return new StepBuilder("completedJobExecutionsStep", jobRepository)
					.tasklet(tasklet, platformTransactionManager)
					.build();
		}
	}


}