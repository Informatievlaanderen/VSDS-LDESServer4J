package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.MemberMetricsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.UnprocessedView;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.UnprocessedViewRepository;
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
import java.util.Objects;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FragmentationServiceTest {
	private static final String COLLECTION = "collection";
	@Mock
	private Step bucketStep;
	@Mock
	private Step paginationStep;
	@Mock
	private MemberMetricsRepository memberMetricsRepository;
	@Mock
	private JobLauncher jobLauncher;
	@Mock
	private JobExplorer jobExplorer;
	@Mock
	private JobRepository jobRepository;
	@Mock
	private UnprocessedViewRepository unprocessedViewRepository;
	@Captor
	private ArgumentCaptor<JobParameters> captor;
	private FragmentationService fragmentationService;
	private List<UnprocessedView> unprocessedViews;

	@BeforeEach
	void setUp() {
		fragmentationService = new FragmentationService(jobLauncher, jobRepository, jobExplorer, bucketStep, paginationStep, unprocessedViewRepository);
		unprocessedViews =  List.of(
				new UnprocessedView(1, COLLECTION, 1, "v1"),
				new UnprocessedView(1, COLLECTION, 2, "v2")
		);
	}

	@Test
	void when_unprocessedViews_then_triggerJobsForEachView() throws Exception {
		when(unprocessedViewRepository.findAll()).thenReturn(unprocessedViews);

		fragmentationService.scheduledJobLauncher();

		verify(jobLauncher, times(2)).run(any(), captor.capture());
		assertThat(captor.getAllValues())
				.map(FragmentationServiceTest::mapParamsToUnprocessedView)
				.containsExactlyInAnyOrderElementsOf(unprocessedViews);
	}

	@Test
	void when_noUnprocessedViews_then_triggerNone() {
		when(unprocessedViewRepository.findAll()).thenReturn(List.of());

		fragmentationService.scheduledJobLauncher();

		verifyNoInteractions(jobLauncher);
	}

	@Test
	void when_unprocessedViews_then_triggerJobsForEachViewThatIsntRunningAlready() throws Exception {
		when(unprocessedViewRepository.findAll()).thenReturn(unprocessedViews);

		JobExecution jobExecution = mock(JobExecution.class);
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong(VIEW_ID, 1L)
				.addLong(COLLECTION_ID, 1L)
				.addString(VIEW_NAME, "v1")
				.addString(COLLECTION_NAME, COLLECTION)
				.toJobParameters();
		when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobExplorer.findRunningJobExecutions(FRAGMENTATION_JOB)).thenReturn(Set.of(jobExecution));

		fragmentationService.scheduledJobLauncher();

		verify(jobLauncher).run(any(), captor.capture());
		assertThat(captor.getValue())
				.extracting(FragmentationServiceTest::mapParamsToUnprocessedView)
				.isEqualTo(new UnprocessedView(1, COLLECTION, 2, "v2"));
	}

	private static UnprocessedView mapParamsToUnprocessedView(JobParameters params) {
		return new UnprocessedView(
				Objects.requireNonNull(params.getLong(COLLECTION_ID)).intValue(),
				params.getString(COLLECTION_NAME),
				Objects.requireNonNull(params.getLong(VIEW_ID)).intValue(),
				params.getString(VIEW_NAME)
		);
	}

}
