package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.ViewNeedsRebucketisationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamClosedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MembersIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BucketProcessor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.junit.jupiter.api.AfterEach;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.batch.core.explore.JobExplorer;
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
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBatchTest
@EnableAutoConfiguration
@ActiveProfiles("test")
@ContextConfiguration(classes = { SpringBatchConfiguration.class, FragmentationService.class, BucketProcessor.class})
class FragmentationServiceTest {

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
				new IngestedMember("x/1", collectionName, versionOf, LocalDateTime.now(), 0L, true, "", null),
				new IngestedMember("x/2", collectionName, versionOf, LocalDateTime.now(), 0L, true, "", null),
				new IngestedMember("x/3", collectionName, versionOf, LocalDateTime.now(), 0L, true, "", null),
				new IngestedMember("x/4", collectionName, versionOf, LocalDateTime.now(), 0L, true, "", null)
		);

		mockBasicViews(2);
		mockReader(members);
		mockWriter();

		fragmentationService.executeFragmentation(new MembersIngestedEvent("collection", List.of()));

		assertEquals(2 * members.size(), output.size());

		output.clear();

		ViewSpecification newView = new ViewSpecification(ViewName.fromString(collectionName+ "/v3"), List.of(), List.of(), 100);
		mockBasicViews(3);

		fragmentationService.handleViewInitializationEvent(new ViewNeedsRebucketisationEvent(newView));

		assertEquals(members.size(), output.size());
	}

	private void mockBasicViews(int count) {
		List<FragmentationStrategyBatchExecutor> fragmentationExecutors = new ArrayList<>();

		for (int i = 1; i <= count; i++) {
			final FragmentationStrategyBatchExecutor executor = mock(FragmentationStrategyBatchExecutor.class);
			when(executor.bucketise(any())).thenReturn(List.of(
					new BucketisedMember("x", new ViewName(collectionName, "v" + i), "v" + i, 0L)));
			fragmentationExecutors.add(executor);
			when(strategyCollection.getFragmentationStrategyExecutor("es/v"+i)).thenReturn(Optional.of(executor));
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
