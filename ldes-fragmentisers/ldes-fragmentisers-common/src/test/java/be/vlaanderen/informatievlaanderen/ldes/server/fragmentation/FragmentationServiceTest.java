package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.MemberMetricsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.ServerMetrics;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BucketJobDefinitions;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BucketProcessors;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.EventStreamProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBatchTest
@EnableAutoConfiguration
@ActiveProfiles("test")
@ContextConfiguration(classes = {SpringBatchConfiguration.class, FragmentationService.class, BucketProcessors.class, BucketJobDefinitions.class })
@TestPropertySource(properties = { "spring.batch.jdbc.initialize-schema=always", "ldes-server.fragmentation-cron=*/1 * * * * *"})
class FragmentationServiceTest {
	private static final int FRAGMENTATION_INTERVAL = 1000;


	@MockBean(name = "viewPartitioner")
	private Partitioner viewPartitioner;
	@MockBean(name = "newMemberReader")
	private ItemReader<FragmentationMember> newMemberReader;
	@MockBean
	ItemWriter<List<BucketisedMember>> itemWriter;
	@MockBean(name = "paginationStep")
	Step paginationStep;
	@MockBean
	ServerMetrics serverMetrics;
	@MockBean
	FragmentationStrategyCollection strategyCollection;
	@MockBean
	MemberMetricsRepository memberMetricsRepository;
	@Autowired
	private FragmentationService fragmentationService;
	@Autowired
	private JobRepositoryTestUtils jobRepositoryTestUtils;


	private final String collectionName = "es";
	private final EventStreamProperties eventStreamProperties = new EventStreamProperties(collectionName, "versionOfPath", "timestampPath", false);
	private final String versionOf = "x";
	private final List<BucketisedMember> output = new ArrayList<>();

	@AfterEach
	public void cleanUp() {
		jobRepositoryTestUtils.removeJobExecutions();
	}

	@Test
	void when_MemberIngestedEvent_then_AllFragmentationExecutorsFromThisCollection_should_BeTriggered() throws Exception {
		final List<FragmentationMember> members = LongStream.range(1, 5)
				.mapToObj(id -> new FragmentationMember(id, "subject", versionOf, LocalDateTime.now(), eventStreamProperties, null))
				.toList();

		mockBasicViews(2);
		mockReader(members);
		mockWriter();

		when(memberMetricsRepository.getUnprocessedCollections()).thenReturn(List.of(new ViewName("collection", "v")));

		fragmentationService.scheduledJobLauncher();

		await().atMost(FRAGMENTATION_INTERVAL * 5, TimeUnit.MILLISECONDS)
				.untilAsserted(() -> assertEquals(2 * members.size(), output.size()));

		output.clear();

		mockBasicViews(3);

		fragmentationService.scheduledJobLauncher();

		await().atMost(FRAGMENTATION_INTERVAL * 5, TimeUnit.MILLISECONDS).untilAsserted(() -> assertEquals(members.size(), output.size()));
	}

	private void mockBasicViews(int count) {
		List<FragmentationStrategyBatchExecutor> fragmentationExecutors = new ArrayList<>();

		for (int i = 1; i <= count; i++) {
			final FragmentationStrategyBatchExecutor executor = mock(FragmentationStrategyBatchExecutor.class);
			when(executor.bucketise(any())).thenReturn(List.of(
					new BucketisedMember(1, 1)));
			fragmentationExecutors.add(executor);
			when(strategyCollection.getFragmentationStrategyExecutor("es/v" + i)).thenReturn(Optional.of(executor));
		}

		when(strategyCollection.getAllFragmentationStrategyExecutors(collectionName)).thenReturn(fragmentationExecutors);
	}

	private void mockReader(List<FragmentationMember> members) throws Exception {
		final FragmentationMember firstMembers = members.getFirst();
		final FragmentationMember[] additionalMembers = members.subList(1, 4).toArray(new FragmentationMember[4]);
		additionalMembers[3] = null;
		when(newMemberReader.read())
				.thenReturn(firstMembers, additionalMembers);
	}


	private void mockWriter() throws Exception {
		doAnswer(invocation -> {
			Chunk<List<BucketisedMember>> items = invocation.getArgument(0);
			output.addAll(items.getItems().stream().flatMap(List::stream).toList());
			return mock();
		}).when(itemWriter).write(any());
	}
}
