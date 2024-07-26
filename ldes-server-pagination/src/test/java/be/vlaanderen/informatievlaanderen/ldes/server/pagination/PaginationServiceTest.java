package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.NewViewBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch.PaginationJobDefinitions;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageNumber;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBatchTest
@EnableAutoConfiguration
@ActiveProfiles("test")
@ContextConfiguration(classes = {SpringBatchConfiguration.class, PaginationService.class, PaginationJobDefinitions.class, })
@TestPropertySource(properties = {"ldes-server.fragmentation-cron=*/1 * * * * *"})
class PaginationServiceTest {
	private static final ViewName VIEW_NAME_1 = new ViewName("es", "v1");
	@MockBean
	private Partitioner bucketisationPartitioner;
	@MockBean
	private ItemReader<Page> pageReader;
	@MockBean
	private ItemProcessor<Page, Page> pageRelationProcessor;
	@MockBean
	private ItemWriter<Page> memberAssigner;
	@Autowired
	private PaginationService paginationService;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	private List<MemberAllocation> output;
	private final Page page = new Page(1,
			"/%s".formatted(VIEW_NAME_1.asString()),
			new Bucket(1, "es/v1"),
			new PageNumber(1),
			50);

	@Test
	void when_MemberBucketised_Then_CorrectServiceCalled() throws Exception {
		eventPublisher.publishEvent(new ViewInitializationEvent(new ViewSpecification(VIEW_NAME_1, List.of(), List.of(), 10)));

		mockBucketisationPartitioner();
		mockReader();
		stubProcessor();


		paginationService.handleMemberBucketisedEvent();

		await()
				.timeout(25, SECONDS)
				.untilAsserted(() -> verify(memberAssigner).write(argThat(chunk -> chunk.getItems().size() == 1)));
	}

	@Test
	void when_ViewDeleted_Then_ServiceRemoved() throws Exception {
		eventPublisher.publishEvent(new ViewInitializationEvent(new ViewSpecification(VIEW_NAME_1, List.of(), List.of(), 10)));

		mockBucketisationPartitioner();
		mockReader();
		stubProcessor();

		paginationService.handleNewViewBucketisedEvent(new NewViewBucketisedEvent(VIEW_NAME_1.asString()));

		verify(memberAssigner).write(argThat(chunk -> chunk.getItems().contains(page)));
	}

	private void mockBucketisationPartitioner() {
		ExecutionContext context = new ExecutionContext();
		context.putLong("bucketId", 1);

		when(bucketisationPartitioner.partition(anyInt())).thenReturn(Map.of("testPartition", context));
	}

	private void mockReader() throws Exception {
		when(pageReader.read())
				.thenReturn(page, null);
	}


	private void stubProcessor() throws Exception {
		when(pageRelationProcessor.process(page)).thenReturn(page);
	}
}