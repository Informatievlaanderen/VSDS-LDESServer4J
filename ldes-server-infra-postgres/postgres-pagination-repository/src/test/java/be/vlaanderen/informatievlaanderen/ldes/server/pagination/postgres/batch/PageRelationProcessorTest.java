package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch.PageRelationProcessor;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.UnpagedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRelationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageAssignment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PageRelationProcessorTest {
	private static final int PAGE_SIZE = 150;
	private static final long BUCKET_ID = 12;
	private static final int UNPROCESSED_MEMBER_COUNT = 32;
	private static final long PARENT_PAGE_ID = 2;
	private static final long CHILD_PAGE_ID = PARENT_PAGE_ID + 1;

	@Mock
	private PageRepository pageRepository;
	@Mock
	private PageRelationRepository pageRelationRepository;
	@InjectMocks
	private PageRelationProcessor pageRelationProcessor;

	@Test
	void given_FullPage_when_Process_then_CreateNewPage() {
		final Page parentPage = new Page(PARENT_PAGE_ID, BUCKET_ID, "/mobility-hindrances/by-page?pageNumber=2", PAGE_SIZE, PAGE_SIZE);
		when(pageRepository.getOpenPage(BUCKET_ID)).thenReturn(parentPage);
		when(pageRepository.createPage(BUCKET_ID, parentPage.createChildPartialUrl().asString())).thenReturn((int) CHILD_PAGE_ID);

		final List<PageAssignment> result = pageRelationProcessor.process(
				Collections.nCopies(UNPROCESSED_MEMBER_COUNT, new UnpagedMember(0, BUCKET_ID)));

		verify(pageRepository).createPage(parentPage.getBucketId(), parentPage.createChildPartialUrl().asString());
		verify(pageRelationRepository).insertGenericBucketRelation(eq(parentPage.getId()), anyLong());
		assertThat(result)
				.isNotNull()
				.filteredOn(PageAssignment::pageId, CHILD_PAGE_ID)
				.filteredOn(PageAssignment::bucketId, BUCKET_ID)
				.hasSize(UNPROCESSED_MEMBER_COUNT);
	}

	@Test
	void given_RootPage_when_Process_then_CreateNewPage() {
		final Page rootPage = new Page(PARENT_PAGE_ID, BUCKET_ID, "/mobility-hindrances/by-page", PAGE_SIZE, 0);
		when(pageRepository.getOpenPage(BUCKET_ID)).thenReturn(rootPage);
		when(pageRepository.createPage(BUCKET_ID, rootPage.createChildPartialUrl().asString())).thenReturn((int) CHILD_PAGE_ID);

		final List<PageAssignment> result = pageRelationProcessor.process(
				Collections.nCopies(UNPROCESSED_MEMBER_COUNT, new UnpagedMember(0, BUCKET_ID)));

		verify(pageRepository).createPage(rootPage.getBucketId(), rootPage.createChildPartialUrl().asString());
		verify(pageRelationRepository).insertGenericBucketRelation(eq(rootPage.getId()), anyLong());
		assertThat(result)
				.isNotNull()
				.filteredOn(PageAssignment::pageId, CHILD_PAGE_ID)
				.filteredOn(PageAssignment::bucketId, BUCKET_ID)
				.hasSize(UNPROCESSED_MEMBER_COUNT);
	}

	@Test
	void given_PageWithSpace_when_Process_then_UseExistingPage() {
		final Page rootPage = new Page(PARENT_PAGE_ID, BUCKET_ID, "/mobility-hindrances/by-page?pageNumber=3", PAGE_SIZE, 100);
		when(pageRepository.getOpenPage(BUCKET_ID)).thenReturn(rootPage);

		final List<PageAssignment> result = pageRelationProcessor.process(
				Collections.nCopies(UNPROCESSED_MEMBER_COUNT, new UnpagedMember(0, BUCKET_ID)));

		assertThat(result)
				.isNotNull()
				.filteredOn(PageAssignment::pageId, PARENT_PAGE_ID)
				.filteredOn(PageAssignment::bucketId, BUCKET_ID)
				.hasSize(UNPROCESSED_MEMBER_COUNT);
	}

	@Test
	void given_PageWithInsufficientSpace_when_Process_then_UseBothExistingAndNewPage() {
		final int alreadyAssignedMemberCount = 132;
		final Page page = new Page(PARENT_PAGE_ID, BUCKET_ID, "/mobility-hindrances/by-page?pageNumber=3", PAGE_SIZE, alreadyAssignedMemberCount);
		when(pageRepository.getOpenPage(BUCKET_ID)).thenReturn(page);
		when(pageRepository.createPage(BUCKET_ID, page.createChildPartialUrl().asString())).thenReturn((int) CHILD_PAGE_ID);

		final List<PageAssignment> result = pageRelationProcessor.process(
				Collections.nCopies(UNPROCESSED_MEMBER_COUNT, new UnpagedMember(0, BUCKET_ID)));

		assertThat(result)
				.isNotNull()
				.filteredOn(PageAssignment::pageId, PARENT_PAGE_ID)
				.filteredOn(PageAssignment::bucketId, BUCKET_ID)
				.hasSize(PAGE_SIZE - alreadyAssignedMemberCount);
		assertThat(result)
				.isNotNull()
				.filteredOn(PageAssignment::pageId, CHILD_PAGE_ID)
				.filteredOn(PageAssignment::bucketId, BUCKET_ID)
				.hasSize(UNPROCESSED_MEMBER_COUNT - (PAGE_SIZE - alreadyAssignedMemberCount));
	}

	@Test
	void given_ZeroAssignedMembers_when_Process_then_ReturnEmptyList() {
		final List<PageAssignment> result = pageRelationProcessor.process(List.of());

		assertThat(result).isEmpty();
	}
}