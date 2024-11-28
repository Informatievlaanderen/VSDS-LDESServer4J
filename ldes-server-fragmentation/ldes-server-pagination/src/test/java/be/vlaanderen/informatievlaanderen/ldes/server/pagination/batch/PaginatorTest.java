package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PartialUrl;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.pagenumber.NumericPageNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaginatorTest {
	private static final long BUCKET_ID = 1;
	private static final String VIEW_NAME = "event-stream/paged";
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

		when(pageMemberRepository.getUnpaginatedMembersForBucket(BUCKET_ID)).thenReturn(List.of());

		paginator.execute(null, chunkContext);

		InOrder inOrder = inOrder(pageMemberRepository, pageRepository);
		inOrder.verify(pageMemberRepository).getUnpaginatedMembersForBucket(BUCKET_ID);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_Paginating_withNumberedOpenPage_expectCorrectPages() {
		mockBucketId();
		Page numberedPage = new Page(1, BUCKET_ID, new PartialUrl(VIEW_NAME, "", new NumericPageNumber(1)), PAGE_SIZE);

		when(pageMemberRepository.getUnpaginatedMembersForBucket(BUCKET_ID)).thenReturn(List.of(1L, 2L, 3L, 4L, 5L));
		when(pageRepository.getOpenPage(BUCKET_ID))
				.thenReturn(numberedPage);

		when(pageRepository.createNextPage(any()))
				.thenReturn(new Page(2, BUCKET_ID, new PartialUrl(VIEW_NAME, "", new NumericPageNumber(2)), PAGE_SIZE))
				.thenReturn(new Page(3, BUCKET_ID, new PartialUrl(VIEW_NAME, "", new NumericPageNumber(3)), PAGE_SIZE));

		paginator.execute(null, chunkContext);

		InOrder inOrder = inOrder(pageMemberRepository, pageRepository);
		inOrder.verify(pageMemberRepository).getUnpaginatedMembersForBucket(BUCKET_ID);
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
		Page unNumberedPage = new Page(0, BUCKET_ID, new PartialUrl(VIEW_NAME, "", null), PAGE_SIZE);
		Page numberedPage = new Page(1, BUCKET_ID, new PartialUrl(VIEW_NAME, "", null), PAGE_SIZE);

		when(pageMemberRepository.getUnpaginatedMembersForBucket(BUCKET_ID)).thenReturn(List.of(1L, 2L, 3L, 4L, 5L));
		when(pageRepository.getOpenPage(BUCKET_ID))
				.thenReturn(unNumberedPage);

		when(pageRepository.createNextPage(any()))
				.thenReturn(numberedPage)
				.thenReturn(new Page(2, BUCKET_ID, new PartialUrl(VIEW_NAME, "", new NumericPageNumber(2)), PAGE_SIZE))
				.thenReturn(new Page(3, BUCKET_ID, new PartialUrl(VIEW_NAME, "", new NumericPageNumber(3)), PAGE_SIZE));

		paginator.execute(null, chunkContext);

		InOrder inOrder = inOrder(pageMemberRepository, pageRepository);
		inOrder.verify(pageMemberRepository).getUnpaginatedMembersForBucket(BUCKET_ID);
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
		Page numberedPage = new Page(1, BUCKET_ID, new PartialUrl(VIEW_NAME, "", new NumericPageNumber(1)), PAGE_SIZE,1);

		when(pageMemberRepository.getUnpaginatedMembersForBucket(BUCKET_ID)).thenReturn(List.of(1L, 2L, 3L, 4L, 5L));
		when(pageRepository.getOpenPage(BUCKET_ID))
				.thenReturn(numberedPage);
		when(pageRepository.createNextPage(any()))
				.thenReturn(new Page(2, BUCKET_ID, new PartialUrl(VIEW_NAME, "", new NumericPageNumber(2)), PAGE_SIZE))
				.thenReturn(new Page(3, BUCKET_ID, new PartialUrl(VIEW_NAME, "", new NumericPageNumber(3)), PAGE_SIZE));

		paginator.execute(null, chunkContext);

		InOrder inOrder = inOrder(pageMemberRepository, pageRepository);
		inOrder.verify(pageMemberRepository).getUnpaginatedMembersForBucket(BUCKET_ID);
		inOrder.verify(pageMemberRepository).assignMembersToPage(pageCaptor.capture(), listCaptor.capture());
		inOrder.verify(pageRepository).createNextPage(any());
		inOrder.verify(pageMemberRepository).assignMembersToPage(pageCaptor.capture(), listCaptor.capture());
		inOrder.verify(pageRepository).createNextPage(any());
		inOrder.verify(pageMemberRepository).assignMembersToPage(pageCaptor.capture(), listCaptor.capture());
		inOrder.verify(pageRepository).createNextPage(any());
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
		Map<String, Object> jobParameters = Map.of("viewId", 1);

		// Set up the mock behavior
		when(chunkContext.getStepContext()).thenReturn(stepContext);
		when(stepContext.getStepExecution()).thenReturn(stepExecution);
		when(stepContext.getJobParameters()).thenReturn(jobParameters);
		when(stepExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.getLong("bucketId")).thenReturn(BUCKET_ID);
	}
}
