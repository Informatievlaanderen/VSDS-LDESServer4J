package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageAssignment;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PartialUrl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch.PageRelationProcessor.INSERT_PAGE_RELATION_SQL;
import static be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch.PageRelationProcessor.SELECT_UNPROCESSED_MEMBER_COUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PageRelationProcessorTest {
	private static final int PAGE_SIZE = 150;
	private static final long BUCKET_ID = 12;
	private static final int UNPROCESSED_MEMBER_COUNT = 32;
	private static final long PARENT_PAGE_ID = 2;
	private static final long CHILD_PAGE_ID = PARENT_PAGE_ID + 1;

	@Mock
	private JdbcTemplate jdbcTemplate;
	@InjectMocks
	private PageRelationProcessor pageRelationProcessor;

	@Test
	void given_FullPage_when_Process_then_CreateNewPage() {
		final PageAssignment expectedPageAssignment = new PageAssignment(CHILD_PAGE_ID, BUCKET_ID, UNPROCESSED_MEMBER_COUNT);
		final Page parentPage = new Page(PARENT_PAGE_ID, BUCKET_ID, "/mobility-hindrances/by-page?pageNumber=2", PAGE_SIZE, PAGE_SIZE);
		when(jdbcTemplate.queryForObject(SELECT_UNPROCESSED_MEMBER_COUNT, Integer.class, BUCKET_ID)).thenReturn(UNPROCESSED_MEMBER_COUNT);
		when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(GeneratedKeyHolder.class)))
				.thenAnswer(invocationOnMock -> {
					invocationOnMock.getArgument(1, GeneratedKeyHolder.class).getKeyList().add(Map.of("page_id", CHILD_PAGE_ID));
					return 1;
				});

		final List<PageAssignment> result = pageRelationProcessor.process(parentPage);

		verify(jdbcTemplate).update(INSERT_PAGE_RELATION_SQL, PARENT_PAGE_ID, CHILD_PAGE_ID);
		assertThat(result).containsExactly(expectedPageAssignment);
	}

	@Test
	void given_RootPage_when_Process_then_CreateNewPage() {
		final PageAssignment expectedPageAssignment = new PageAssignment(CHILD_PAGE_ID, BUCKET_ID, UNPROCESSED_MEMBER_COUNT);
		final Page rootPage = new Page(PARENT_PAGE_ID, BUCKET_ID, "/mobility-hindrances/by-page", PAGE_SIZE, 0);
		when(jdbcTemplate.queryForObject(SELECT_UNPROCESSED_MEMBER_COUNT, Integer.class, BUCKET_ID)).thenReturn(UNPROCESSED_MEMBER_COUNT);
		when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(GeneratedKeyHolder.class)))
				.thenAnswer(invocationOnMock -> {
					invocationOnMock.getArgument(1, GeneratedKeyHolder.class).getKeyList().add(Map.of("page_id", CHILD_PAGE_ID));
					return 1;
				});

		final List<PageAssignment> result = pageRelationProcessor.process(rootPage);

		verify(jdbcTemplate).update(INSERT_PAGE_RELATION_SQL, PARENT_PAGE_ID, CHILD_PAGE_ID);
		assertThat(result).containsExactly(expectedPageAssignment);
	}

	@Test
	void given_PageWithSpace_when_Process_then_UseExistingPage() {
		final PageAssignment expectedPageAssignment = new PageAssignment(PARENT_PAGE_ID, BUCKET_ID, UNPROCESSED_MEMBER_COUNT);
		final Page rootPage = new Page(PARENT_PAGE_ID, BUCKET_ID, "/mobility-hindrances/by-page?pageNumber=3", PAGE_SIZE, 100);
		when(jdbcTemplate.queryForObject(SELECT_UNPROCESSED_MEMBER_COUNT, Integer.class, BUCKET_ID)).thenReturn(UNPROCESSED_MEMBER_COUNT);

		final List<PageAssignment> result = pageRelationProcessor.process(rootPage);

		verify(jdbcTemplate).queryForObject(SELECT_UNPROCESSED_MEMBER_COUNT, Integer.class, BUCKET_ID);
		verifyNoMoreInteractions(jdbcTemplate);
		assertThat(result).containsExactly(expectedPageAssignment);
	}

	@Test
	void given_PageWithInsufficientSpace_when_Process_then_UseBothExistingAndNewPage() {
		final int alreadyAssignedMemberCount = 132;
		final List<PageAssignment> expectedPageAssignments = List.of(
				new PageAssignment(PARENT_PAGE_ID, BUCKET_ID, PAGE_SIZE - alreadyAssignedMemberCount),
				new PageAssignment(CHILD_PAGE_ID, BUCKET_ID, UNPROCESSED_MEMBER_COUNT - (PAGE_SIZE - alreadyAssignedMemberCount))
		);
		final Page page = new Page(PARENT_PAGE_ID, BUCKET_ID, "/mobility-hindrances/by-page?pageNumber=3", PAGE_SIZE, alreadyAssignedMemberCount);
		when(jdbcTemplate.queryForObject(SELECT_UNPROCESSED_MEMBER_COUNT, Integer.class, BUCKET_ID)).thenReturn(UNPROCESSED_MEMBER_COUNT);
		when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(GeneratedKeyHolder.class)))
				.thenAnswer(invocationOnMock -> {
					invocationOnMock.getArgument(1, GeneratedKeyHolder.class).getKeyList().add(Map.of("page_id", CHILD_PAGE_ID));
					return 1;
				});

		final List<PageAssignment> result = pageRelationProcessor.process(page);

		assertThat(result).containsExactlyElementsOf(expectedPageAssignments);
	}

	@Test
	void given_ZeroAssignedMembers_when_Process_then_ReturnEmptyList() {
		when(jdbcTemplate.queryForObject(SELECT_UNPROCESSED_MEMBER_COUNT, Integer.class, BUCKET_ID)).thenReturn(0);

		final List<PageAssignment> result = pageRelationProcessor.process(new Page(PARENT_PAGE_ID, 12, (PartialUrl) null, PAGE_SIZE));

		assertThat(result).isEmpty();
	}
}