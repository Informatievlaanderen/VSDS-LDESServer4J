package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.exceptions.MaintenanceJobException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.maintenance.batch.MaintenanceFlows.MAINTENANCE_JOB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceServiceTest {
	@Mock
	private FlowJobBuilder flowJobBuilder;
	@Mock
	private JobLauncher jobLauncher;
	@Mock
	private Job maintenanceJob;
	@Mock(answer = Answers.RETURNS_MOCKS)
	private JobExplorer jobExplorer;
	@InjectMocks
	private MaintenanceService maintenanceService;
	@Captor
	private ArgumentCaptor<JobParameters> jobParametersCaptor;


	@Test
	void given_NoRunningJobs_when_ScheduleJob_then_JobIsScheduled() throws JobExecutionException {
		maintenanceService.scheduleMaintenanceJob();

		verify(jobLauncher).run(any(), jobParametersCaptor.capture());
	}

	@Test
	void given_JobsIsRunning_when_ScheduleJob_then_JobIsNotScheduled() {
		when(jobExplorer.findRunningJobExecutions(MAINTENANCE_JOB)).thenReturn(Set.of(new JobExecution(1L)));

		maintenanceService.scheduleMaintenanceJob();

		verifyNoInteractions(jobLauncher);
		assertThat(jobParametersCaptor.getAllValues()).isEmpty();
	}

	@ParameterizedTest
	@ValueSource(classes = {JobExecutionAlreadyRunningException.class, JobInstanceAlreadyCompleteException.class, JobParametersInvalidException.class, JobRestartException.class})
	void when_JobExecutionExceptionIsThrownDuringLaunch_then_ThrowMaintenanceException(Class<? extends JobExecutionException> exceptionClass) throws JobExecutionException {
		when(jobLauncher.run(any(), any())).thenThrow(exceptionClass);

		assertThatThrownBy(maintenanceService::scheduleMaintenanceJob)
				.isInstanceOf(MaintenanceJobException.class)
				.hasCauseInstanceOf(exceptionClass);
		verify(jobLauncher).run(any(), jobParametersCaptor.capture());
		assertThat(jobParametersCaptor.getValue())
				.extracting(jobParams -> jobParams.getLocalDateTime("triggered"))
				.isNotNull();
	}
}