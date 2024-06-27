package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamClosedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.ViewNeedsRebucketisationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BucketProcessor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.membermapper.MemberMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.membermapper.MemberMapperCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
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

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBatchTest
@EnableAutoConfiguration
@ActiveProfiles("test")
@ContextConfiguration(classes = {SpringBatchConfiguration.class, FragmentationService.class, BucketProcessor.class})
@TestPropertySource(properties = { "ldes-server.fragmentation-cron=*/1 * * * * *" })
class FragmentationServiceTest {
	private static final int FRAGMENTATION_INTERVAL = 1000;

	@MockBean(name = "newMemberReader")
	ItemReader<IngestedMember> newMemberItemReader;

	@MockBean(name = "refragmentEventStream")
	ItemReader<IngestedMember> refragmentItemReader;

	@MockBean
	private FragmentRepository fragmentRepository;

	@MockBean
	ItemWriter<List<BucketisedMember>> itemWriter;

	@MockBean
	FragmentationStrategyCollection strategyCollection;

	@MockBean
	MemberMapperCollection memberMappers;

	@Autowired
	private FragmentationService fragmentationService;

	@Autowired
	private JobRepositoryTestUtils jobRepositoryTestUtils;

	private final String collectionName = "es";
	private final String versionOf = "x";
	private final List<BucketisedMember> output = new ArrayList<>();

	@AfterEach
	public void cleanUp() {
		jobRepositoryTestUtils.removeJobExecutions();
	}

	@Test
	void when_MemberIngestedEvent_then_AllFragmentationExecutorsFromThisCollection_should_BeTriggered() throws Exception {
		List<IngestedMember> members = List.of(
				new IngestedMember("x/1", collectionName, versionOf, LocalDateTime.now(), true, "", null),
				new IngestedMember("x/2", collectionName, versionOf, LocalDateTime.now(), true, "", null),
				new IngestedMember("x/3", collectionName, versionOf, LocalDateTime.now(), true, "", null),
				new IngestedMember("x/4", collectionName, versionOf, LocalDateTime.now(), true, "", null)
		);

		MemberMapper memberMapper = mock(MemberMapper.class);
		when(memberMappers.getMemberMapper(collectionName)).thenReturn(Optional.of(memberMapper));

		mockBasicViews(2);
		mockReader(members);
		mockWriter();

		fragmentationService.executeFragmentation();

		await().atMost(FRAGMENTATION_INTERVAL, TimeUnit.MILLISECONDS)
				.untilAsserted(() -> assertEquals(2 * members.size(), output.size()));

		output.clear();

		ViewSpecification newView = new ViewSpecification(ViewName.fromString(collectionName + "/v3"), List.of(), List.of(), 100);
		mockBasicViews(3);

		fragmentationService.handleViewInitializationEvent(new ViewNeedsRebucketisationEvent(newView.getName()));

		await().atMost(FRAGMENTATION_INTERVAL, TimeUnit.MILLISECONDS).untilAsserted(() -> assertEquals(members.size(), output.size()));
		verify(memberMapper, times(members.size() * 3))
				.mapToFragmentationMember(any());
	}

	private void mockBasicViews(int count) {
		List<FragmentationStrategyBatchExecutor> fragmentationExecutors = new ArrayList<>();

		for (int i = 1; i <= count; i++) {
			final FragmentationStrategyBatchExecutor executor = mock(FragmentationStrategyBatchExecutor.class);
			when(executor.bucketise(any())).thenReturn(List.of(
					new BucketisedMember("x", new ViewName(collectionName, "v" + i), "v" + i)));
			fragmentationExecutors.add(executor);
			when(strategyCollection.getFragmentationStrategyExecutor("es/v" + i)).thenReturn(Optional.of(executor));
		}

		when(strategyCollection.getFragmentationStrategyExecutors(collectionName)).thenReturn(fragmentationExecutors);
	}

	private void mockReader(List<IngestedMember> members) throws Exception {
		when(newMemberItemReader.read())
				.thenReturn(members.get(0), members.get(1), members.get(2), members.get(3), null);
		when(refragmentItemReader.read())
				.thenReturn(members.get(0), members.get(1), members.get(2), members.get(3), null);
	}

	private void mockWriter() throws Exception {
		doAnswer(invocation -> {
			Chunk<List<BucketisedMember>> items = invocation.getArgument(0);
			output.addAll(items.getItems().stream().flatMap(List::stream).toList());
			return null;
		}).when(itemWriter).write(any());
	}

	@Test
	void when_EventStreamClosedEvent_then_FragmentsAreMadeImmutable() {
		EventStreamClosedEvent event = new EventStreamClosedEvent("collectionName");

		fragmentationService.markFragmentsImmutableInCollection(event);

		verify(fragmentRepository).markFragmentsImmutableInCollection("collectionName");
	}
}
