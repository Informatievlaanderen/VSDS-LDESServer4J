package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MembersBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.NewViewBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.ViewBucketisationService;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch.PaginationProcessor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier.fromFragmentId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBatchTest
@EnableAutoConfiguration
@ActiveProfiles("test")
@ContextConfiguration(classes = {SpringBatchConfiguration.class, PaginationService.class, PaginationProcessor.class,
		MemberPaginationServiceCreator.class, ViewBucketisationService.class})
class PaginationServiceTest {
	private final ViewName VIEW_NAME_1 = new ViewName("es", "v1");
	@MockBean(name = "bucketisationPartitioner")
	private Partitioner bucketisationPartitioner;
	@MockBean(name = "viewBucketisationPartitioner")
	private Partitioner viewBucketisationPartitioner;
	@MockBean
	private ItemReader<List<BucketisedMember>> reader;
	@MockBean
	private ItemWriter<List<MemberAllocation>> writer;
	@MockBean
	private FragmentRepository fragmentRepository;
	@Autowired
	private PaginationService paginationService;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	private List<MemberAllocation> output;

	@Test
	void when_MemberBucketised_Then_CorrectServiceCalled() throws Exception {
		when(fragmentRepository.retrieveFragment(any())).thenReturn(Optional.of(new Fragment(fromFragmentId(VIEW_NAME_1.asString()))));
		eventPublisher.publishEvent(new ViewInitializationEvent(new ViewSpecification(VIEW_NAME_1, List.of(), List.of(), 10)));

		mockBucketisationPartitioner();
		mockReader();
		mockWriter();


		paginationService.handleMemberBucketisedEvent(new MembersBucketisedEvent());

		await()
				.timeout(25, SECONDS)
				.untilAsserted(() -> assertEquals(4, output.size()));
	}

	@Test
	void when_ViewDeleted_Then_ServiceRemoved() throws Exception {
		when(fragmentRepository.retrieveFragment(any())).thenReturn(Optional.of(new Fragment(fromFragmentId(VIEW_NAME_1.asString()))));
		eventPublisher.publishEvent(new ViewInitializationEvent(new ViewSpecification(VIEW_NAME_1, List.of(), List.of(), 10)));

		mockViewBucketisationPartitioner();
		mockReader();
		mockWriter();

		paginationService.handleNewViewBucketisedEvent(new NewViewBucketisedEvent(VIEW_NAME_1.asString()));

		assertEquals(4, output.size());
	}

	private void mockBucketisationPartitioner() {
		ExecutionContext context = new ExecutionContext();
		context.putString("viewName", VIEW_NAME_1.asString());
		context.putString("fragmentId", VIEW_NAME_1.asString());

		when(bucketisationPartitioner.partition(anyInt())).thenReturn(Map.of("testPartition", context));
	}

	private void mockViewBucketisationPartitioner() {
		ExecutionContext context = new ExecutionContext();
		context.putString("fragmentId", VIEW_NAME_1.asString());

		when(viewBucketisationPartitioner.partition(anyInt())).thenReturn(Map.of("testPartition", context));
	}

	private void mockReader() throws Exception {
		when(reader.read()).thenReturn(bucketisedMembers(), null);
	}

	private void mockWriter() throws Exception {
		output = new ArrayList<>();
		doAnswer(invocation -> {
			Chunk<List<MemberAllocation>> items = invocation.getArgument(0);
			output.addAll(items.getItems().stream().flatMap(List::stream).toList());
			return null;
		}).when(writer).write(any());
	}

	private List<BucketisedMember> bucketisedMembers() {
		return List.of(
				new BucketisedMember("x/1", VIEW_NAME_1, "es/v1", 0L),
				new BucketisedMember("x/2", VIEW_NAME_1, "es/v1", 0L),
				new BucketisedMember("x/3", VIEW_NAME_1, "es/v1", 0L),
				new BucketisedMember("x/4", VIEW_NAME_1, "es/v1", 0L)
		);
	}
}