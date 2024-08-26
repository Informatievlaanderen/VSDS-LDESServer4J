package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.MemberMetricsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.ServerMetrics;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.COLLECTION_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.VIEW_NAME;
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
	@MockBean
	JobLauncher jobLauncher;
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


}
