package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.FragmentationMetricsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BucketStepDefinitions.BUCKETISATION_STEP;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PaginationMetricUpdaterTest {
	private static final String COLLECTION = "observations";
	private static final long VIEW_ID = 1L;
	private final JobParameters jobParams = new JobParametersBuilder()
			.addLong("viewId", VIEW_ID)
			.addString("collectionName", COLLECTION)
			.toJobParameters();
	private final JobExecution jobExecution = new JobExecution(1L, jobParams);
	@Mock
	private JdbcTemplate jdbcTemplate;
	@Mock
	private FragmentationMetricsService fragmentationMetricsService;
	@InjectMocks
	private PaginationMetricUpdater paginationMetricUpdater;

	@Test
	void test_AfterJob() {
		final long bucketisedMemberCount = 2;
		StepExecution stepExecution = new StepExecution(BUCKETISATION_STEP, jobExecution);
		stepExecution.setWriteCount(bucketisedMemberCount);
		jobExecution.addStepExecutions(List.of(stepExecution));

		paginationMetricUpdater.afterJob(jobExecution);

		verify(fragmentationMetricsService).updatePaginationCounts(COLLECTION);
		verify(jdbcTemplate).update(anyString(), eq(bucketisedMemberCount), eq(VIEW_ID));
	}

	@Test
	void given_MissingStep_when_afterJob_then_AddZero() {
		StepExecution stepExecution = new StepExecution("fantasy step", jobExecution);
		jobExecution.addStepExecutions(List.of(stepExecution));

		paginationMetricUpdater.afterJob(jobExecution);

		verify(fragmentationMetricsService).updatePaginationCounts(COLLECTION);
		verify(jdbcTemplate).update(anyString(), eq(0L), eq(VIEW_ID));
	}

	@Test
	void given_MissingViewId_when_afterJob_then_ThrowNullPointerException() {
		StepExecution stepExecution = new StepExecution("fantasy step", jobExecution);
		jobExecution.addStepExecutions(List.of(stepExecution));

		assertThatNullPointerException().isThrownBy(() -> paginationMetricUpdater.afterJob(new JobExecution(2L)));
		verifyNoInteractions(fragmentationMetricsService, jdbcTemplate);
	}

	@Test
	void given_MissingCollection_when_afterJob_then_ThrowNullPointerException() {
		JobParameters viewIdJobParam = new JobParametersBuilder().addLong("viewId", VIEW_ID).toJobParameters();
		StepExecution stepExecution = new StepExecution("fantasy step", jobExecution);
		jobExecution.addStepExecutions(List.of(stepExecution));


		assertThatNullPointerException().isThrownBy(() -> paginationMetricUpdater.afterJob(new JobExecution(2L, viewIdJobParam)));
		verifyNoInteractions(fragmentationMetricsService, jdbcTemplate);
	}
}