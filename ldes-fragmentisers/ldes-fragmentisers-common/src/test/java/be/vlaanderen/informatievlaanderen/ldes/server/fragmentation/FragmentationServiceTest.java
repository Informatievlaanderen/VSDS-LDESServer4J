package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.MemberMetricsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FragmentationServiceTest {
	@Mock
	Step bucketStep;
	@Mock
	Step paginationStep;
	@Mock
	MemberMetricsRepository memberMetricsRepository;
	@Mock
	JobLauncher jobLauncher;
	@Mock
	JobExplorer jobExplorer;
	@Mock
	JobRepository jobRepository;
	private FragmentationService fragmentationService;
	@Captor
	private ArgumentCaptor<JobParameters> captor;


	@BeforeEach
	void setUp() {
		fragmentationService = new FragmentationService(jobLauncher, jobRepository, jobExplorer, bucketStep, paginationStep, memberMetricsRepository);
	}

	@Test
	void when_unprocessedViews_then_triggerJobsForEachView() throws Exception {
		String collection = "collection";
		List<ViewName> viewNames = List.of(new ViewName(collection, "v1"), new ViewName(collection, "v2"));

		when(memberMetricsRepository.getUnprocessedViews()).thenReturn(viewNames);

		fragmentationService.scheduledJobLauncher();

		verify(jobLauncher, times(2)).run(any(), captor.capture());
		assertThat(captor.getAllValues())
				.map(params -> new ViewName(params.getString(COLLECTION_NAME), params.getString(VIEW_NAME)))
				.containsExactlyInAnyOrderElementsOf(viewNames);
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
				.thenReturn(List.of(new ViewName(collection, "v1"), new ViewName(collection, "v2")));

		JobExecution jobExecution = mock(JobExecution.class);
		JobParameters jobParameters = new JobParametersBuilder()
				.addString(VIEW_NAME, "v1")
				.addString(COLLECTION_NAME, collection)
				.toJobParameters();
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobExplorer.findRunningJobExecutions(FRAGMENTATION_JOB)).thenReturn(Set.of(jobExecution));

		fragmentationService.scheduledJobLauncher();

		verify(jobLauncher).run(any(), captor.capture());
		assertThat(captor.getValue())
				.extracting(params -> new ViewName(params.getString(COLLECTION_NAME), params.getString(VIEW_NAME)))
				.isEqualTo(new ViewName(collection, "v2"));
	}


}
