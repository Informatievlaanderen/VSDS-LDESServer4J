package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.FragmentationMetricsService;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BucketStepDefinitions.BUCKETISATION_STEP;

@Component
public class PaginationMetricUpdater implements JobExecutionListener {
	private final JdbcTemplate jdbcTemplate;
	private final FragmentationMetricsService fragmentationMetricsService;

	public PaginationMetricUpdater(JdbcTemplate jdbcTemplate, FragmentationMetricsService fragmentationMetricsService) {
		this.jdbcTemplate = jdbcTemplate;
		this.fragmentationMetricsService = fragmentationMetricsService;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		final long bucketisedMemberCount = getBucketisationStepExecution(jobExecution).getWriteCount();
		updateViewStats(bucketisedMemberCount, Objects.requireNonNull(jobExecution.getJobParameters().getLong("viewId")));
		fragmentationMetricsService.updatePaginationCounts(jobExecution.getJobParameters().getString("collectionName"));
	}


	private StepExecution getBucketisationStepExecution(JobExecution jobExecution) {
		return jobExecution.getStepExecutions().stream()
				.filter(stepExecution -> stepExecution.getStepName().equals(BUCKETISATION_STEP))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No step execution found"));
	}

	private void updateViewStats(long uniqueMemberCount, long viewId) {
		String sql = """
				UPDATE view_stats vs
				SET paginated_count = vs.paginated_count + ?
				where view_id = ?;
				""";
		jdbcTemplate.update(sql, uniqueMemberCount, viewId);
	}
}
