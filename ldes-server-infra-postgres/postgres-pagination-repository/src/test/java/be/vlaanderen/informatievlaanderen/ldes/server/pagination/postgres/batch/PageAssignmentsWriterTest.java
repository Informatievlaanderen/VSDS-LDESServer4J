package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageAssignment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PageAssignmentsWriterTest {
	private static final long BUCKET_ID = 123;
	private static final long PAGE_ID = 1234;
	private static final long CHILD_PAGE_ID = 12345;

	@Mock
	private JdbcTemplate jdbcTemplate;
	@InjectMocks
	private PageAssignmentsWriter pageAssignmentsWriter;

	@Test
	void test_write() throws Exception {
		Chunk<List<PageAssignment>> chunk = Chunk.of(
				List.of(new PageAssignment(PAGE_ID, BUCKET_ID, 34),
						new PageAssignment(CHILD_PAGE_ID, BUCKET_ID, 12))
		);
		final List<Object[]> batchArgs = List.of(
				new Object[]{PAGE_ID, BUCKET_ID, 34},
				new Object[]{CHILD_PAGE_ID, BUCKET_ID, 12}
		);

		pageAssignmentsWriter.write(chunk);

//		verify(jdbcTemplate).batchUpdate(eq(SQL), assertArg((List<Object[]> actual) -> {
//			assertThat(actual)
//					.usingRecursiveComparison()
//					.isEqualTo(batchArgs);
//		}));
	}

	@Test
	void given_emptyChunk_test_write() throws Exception {
		pageAssignmentsWriter.write(new Chunk<>());

		verifyNoInteractions(jdbcTemplate);
	}

	@Test
	void given_chunkWithEmptyLists_test_write() throws Exception {
		pageAssignmentsWriter.write(Chunk.of(List.of()));

		verifyNoInteractions(jdbcTemplate);
	}
}