package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageNumber;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PartialUrl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaginatorTest {
	private static final long BUCKET = 1;
	private static final String VIEW = "event-stream/paged";
	private static final int PAGE_SIZE = 2;
	@Mock
	private ChunkContext chunkContext;
	@Mock
	private PageMemberRepository pageMemberRepository;
	@Mock
	private PageRepository pageRepository;
	@InjectMocks
	Paginator paginator;
	@Captor
	ArgumentCaptor<Page> pageCaptor;
	@Captor
	ArgumentCaptor<List<Long>> listCaptor;

	@Test
	void when_Paginating_withNoUnexpectedMembers_doNothing() {
		mockBucketId();

		when(pageMemberRepository.getUnpaginatedMembersForBucket(BUCKET)).thenReturn(List.of());

		paginator.execute(null, chunkContext);

		InOrder inOrder = inOrder(pageMemberRepository, pageRepository);
		inOrder.verify(pageMemberRepository).getUnpaginatedMembersForBucket(BUCKET);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_Paginating_withNumberedOpenPage_expectCorrectPages() {
		mockBucketId();
		Page numberedPage = new Page(1, BUCKET, new PartialUrl(VIEW, "", new PageNumber(1)), PAGE_SIZE);

		when(pageMemberRepository.getUnpaginatedMembersForBucket(BUCKET)).thenReturn(List.of(1L, 2L, 3L, 4L, 5L));
		when(pageRepository.getOpenPage(BUCKET)).thenReturn(numberedPage);

		when(pageMemberRepository.assignMembersToPage(any(), anyList()))
				.thenReturn(new Page(2, BUCKET, new PartialUrl(VIEW, "", new PageNumber(2)), PAGE_SIZE))
				.thenReturn(new Page(3, BUCKET, new PartialUrl(VIEW, "", new PageNumber(3)), PAGE_SIZE));

		paginator.execute(null, chunkContext);

		InOrder inOrder = inOrder(pageMemberRepository, pageRepository);
		inOrder.verify(pageMemberRepository).getUnpaginatedMembersForBucket(BUCKET);
		inOrder.verify(pageMemberRepository, times(3)).assignMembersToPage(pageCaptor.capture(), listCaptor.capture());
		inOrder.verifyNoMoreInteractions();

		assertThat(pageCaptor.getAllValues().stream().map(Page::getId).toList())
				.containsExactlyInAnyOrder(1L, 2L, 3L);
		assertThat(listCaptor.getAllValues().stream().map(List::size).toList())
				.containsExactlyInAnyOrder(2,2,1);
	}

	@Test
	void when_Paginating_withUnNumberedOpenPage_expectCorrectPages() {
		mockBucketId();
		Page unNumberedPage = new Page(0, BUCKET, new PartialUrl(VIEW, "", null), PAGE_SIZE);
		Page numberedPage = new Page(1, BUCKET, new PartialUrl(VIEW, "", null), PAGE_SIZE);

		when(pageMemberRepository.getUnpaginatedMembersForBucket(BUCKET)).thenReturn(List.of(1L, 2L, 3L, 4L, 5L));
		when(pageRepository.getOpenPage(BUCKET)).thenReturn(unNumberedPage);

		when(pageRepository.createNewPage(unNumberedPage)).thenReturn(numberedPage);

		when(pageMemberRepository.assignMembersToPage(any(), anyList()))
				.thenReturn(new Page(2, BUCKET, new PartialUrl(VIEW, "", new PageNumber(2)), PAGE_SIZE))
				.thenReturn(new Page(3, BUCKET, new PartialUrl(VIEW, "", new PageNumber(3)), PAGE_SIZE));

		paginator.execute(null, chunkContext);

		InOrder inOrder = inOrder(pageMemberRepository, pageRepository);
		inOrder.verify(pageMemberRepository).getUnpaginatedMembersForBucket(BUCKET);
		inOrder.verify(pageMemberRepository, times(3)).assignMembersToPage(pageCaptor.capture(), listCaptor.capture());
		inOrder.verifyNoMoreInteractions();

		assertThat(pageCaptor.getAllValues().stream().map(Page::getId).toList())
				.containsExactlyInAnyOrder(1L, 2L, 3L);
		assertThat(listCaptor.getAllValues().stream().map(List::size).toList())
				.containsExactlyInAnyOrder(2,2,1);
	}

	@Test
	void when_Paginating_withSemiFilledNumberedOpenPage_expectCorrectPages() {
		mockBucketId();
		Page numberedPage = new Page(1, BUCKET, new PartialUrl(VIEW, "", new PageNumber(1)), PAGE_SIZE,1);

		when(pageMemberRepository.getUnpaginatedMembersForBucket(BUCKET)).thenReturn(List.of(1L, 2L, 3L, 4L, 5L));
		when(pageRepository.getOpenPage(BUCKET)).thenReturn(numberedPage);

		when(pageMemberRepository.assignMembersToPage(any(), anyList()))
				.thenReturn(new Page(2, BUCKET, new PartialUrl(VIEW, "", new PageNumber(2)), PAGE_SIZE))
				.thenReturn(new Page(3, BUCKET, new PartialUrl(VIEW, "", new PageNumber(3)), PAGE_SIZE));

		paginator.execute(null, chunkContext);

		InOrder inOrder = inOrder(pageMemberRepository, pageRepository);
		inOrder.verify(pageMemberRepository).getUnpaginatedMembersForBucket(BUCKET);
		inOrder.verify(pageMemberRepository, times(3)).assignMembersToPage(pageCaptor.capture(), listCaptor.capture());
		inOrder.verifyNoMoreInteractions();

		assertThat(pageCaptor.getAllValues().stream().map(Page::getId).toList())
				.containsExactlyInAnyOrder(1L, 2L, 3L);
		assertThat(listCaptor.getAllValues().stream().map(List::size).toList())
				.containsExactlyInAnyOrder(1,2,2);
	}

	private void mockBucketId() {
		StepContext stepContext = mock(StepContext.class);
		StepExecution stepExecution = mock(StepExecution.class);
		ExecutionContext executionContext = mock(ExecutionContext.class);

		// Set up the mock behavior
		when(chunkContext.getStepContext()).thenReturn(stepContext);
		when(stepContext.getStepExecution()).thenReturn(stepExecution);
		when(stepExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.getLong("bucketId")).thenReturn(BUCKET);
	}
}
