package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.MemberMetricsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.ServerMetrics;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BatchConfiguration.ASYNC_JOB_LAUNCHER;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BucketJobDefinitions.BUCKETISATION_STEP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {SpringBatchConfiguration.class, FragmentationService.class })
@TestPropertySource(properties = { "ldes-server.fragmentation-cron=*/1 * * * * *"})
class FragmentationServiceTest {
	@MockBean(name = BUCKETISATION_STEP)
	Step bucketStep;
	@MockBean(name = "paginationStep")
	Step paginationStep;
	@MockBean
	ServerMetrics serverMetrics;
	@MockBean
	FragmentationStrategyCollection strategyCollection;
	@MockBean
	MemberMetricsRepository memberMetricsRepository;
	@MockBean(name = ASYNC_JOB_LAUNCHER)
	JobLauncher jobLauncher;
	@MockBean
	JobExplorer jobExplorer;
	@Autowired
	private FragmentationService fragmentationService;

	@Test
	void when_unprocessedViews_then_triggerJobsForEachView() throws Exception {
		String collection = "collection";

		when(memberMetricsRepository.getUnprocessedViews())
				.thenReturn(List.of(new ViewName(collection, "v1"),
						new ViewName(collection, "v2")));

		fragmentationService.scheduledJobLauncher();

		ArgumentCaptor<JobParameters> captor = ArgumentCaptor.forClass(JobParameters.class);
		verify(jobLauncher, times(2)).run(any(), captor.capture());
		assertThat(captor.getAllValues())
				.extracting(obj -> obj.getString(COLLECTION_NAME), obj -> obj.getString(VIEW_NAME))
				.containsExactlyInAnyOrder(
						tuple(collection, "v1"),
						tuple(collection, "v2")
				);
	}

	@Test
	void when_noUnprocessedViews_then_triggerNone() {
		when(memberMetricsRepository.getUnprocessedViews()).thenReturn(List.of());

		fragmentationService.scheduledJobLauncher();

		verifyNoInteractions(jobLauncher);
	}

	@Test
	void when_unprocessedViews_then_triggerJobsForEachViewThatIsntRunningAlready() throws Exception {
		String collection = "collection";

		when(memberMetricsRepository.getUnprocessedViews())
				.thenReturn(List.of(new ViewName(collection, "v1"),
						new ViewName(collection, "v2")));

		JobExecution jobExecution = mock(JobExecution.class);
		JobParameters jobParameters = new JobParametersBuilder()
				.addString(VIEW_NAME, "v1")
				.addString(COLLECTION_NAME, collection)
				.toJobParameters();
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobExplorer.findRunningJobExecutions(FRAGMENTATION_JOB)).thenReturn(Set.of(jobExecution));

		fragmentationService.scheduledJobLauncher();

		ArgumentCaptor<JobParameters> captor = ArgumentCaptor.forClass(JobParameters.class);
		verify(jobLauncher, times(1)).run(any(), captor.capture());
		assertThat(captor.getAllValues())
				.extracting(obj -> obj.getString(COLLECTION_NAME), obj -> obj.getString(VIEW_NAME))
				.containsExactlyInAnyOrder(
						tuple(collection, "v2")
				);
	}


}
