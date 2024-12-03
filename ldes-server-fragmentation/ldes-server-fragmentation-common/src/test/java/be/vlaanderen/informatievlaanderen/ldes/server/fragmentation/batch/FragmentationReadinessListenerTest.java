package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.ContinueFragmentationTriggerEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FragmentationReadinessListenerTest {
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;
	@Mock
	private JobExecution jobExecution;
	@Mock
	private StepExecution stepExecution;
	@InjectMocks
	private FragmentationReadinessListener fragmentationReadinessListener;

	@BeforeEach
	void setUp() {
		when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
	}

	@Test
	void when_AfterJob_then_PublishEvent() {
		final JobParameters jobParameters = new JobParametersBuilder().addLong("viewId", 2L).toJobParameters();
		when(stepExecution.getReadCount()).thenReturn(2L);
		when(jobExecution.isRunning()).thenReturn(false);
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);

		fragmentationReadinessListener.afterJob(jobExecution);

		verify(applicationEventPublisher).publishEvent(assertArg((ContinueFragmentationTriggerEvent event) -> assertThat(event)
				.usingRecursiveComparison()
				.isEqualTo(new ContinueFragmentationTriggerEvent(jobParameters))
		));
	}

	@Test
	void when_StepDidNotReadAnything_then_DoNothing() {
		when(stepExecution.getReadCount()).thenReturn(0L);

		fragmentationReadinessListener.afterJob(jobExecution);

		verifyNoInteractions(applicationEventPublisher);
	}

	@Test
	void when_JobIsRunning_then_DoNothing() {
		when(stepExecution.getReadCount()).thenReturn(2L);
		when(jobExecution.isRunning()).thenReturn(true);

		fragmentationReadinessListener.afterJob(jobExecution);

		verifyNoInteractions(applicationEventPublisher);
	}
}