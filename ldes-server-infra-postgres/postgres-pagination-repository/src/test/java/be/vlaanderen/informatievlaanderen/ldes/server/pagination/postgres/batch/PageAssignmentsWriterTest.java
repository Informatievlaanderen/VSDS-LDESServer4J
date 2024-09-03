package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageAssignment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PageAssignmentsWriterTest {
	private static final long BUCKET_ID = 123;
	private static final long PAGE_ID = 1234;
	private static final long CHILD_PAGE_ID = 12345;

	@Mock
	private JdbcBatchItemWriter<PageAssignment> delegateWriter;
	private PageAssignmentsWriter pageAssignmentsWriter;
	@Captor
	private ArgumentCaptor<Chunk<? extends PageAssignment>> captor;

	@BeforeEach
	public void setup() {
		pageAssignmentsWriter = new PageAssignmentsWriter(delegateWriter);
	}

	@Test
	void test_write() throws Exception {
		Chunk<List<PageAssignment>> chunk = Chunk.of(
				List.of(new PageAssignment(PAGE_ID, BUCKET_ID, 1),
						new PageAssignment(PAGE_ID, BUCKET_ID, 2)),
				List.of(new PageAssignment(CHILD_PAGE_ID, BUCKET_ID, 3),
						new PageAssignment(CHILD_PAGE_ID, BUCKET_ID, 4))
		);

		pageAssignmentsWriter.write(chunk);

		verify(delegateWriter).write(captor.capture());
		assertThat(captor.getValue().size()).isEqualTo(4);
	}

	@Test
	void given_emptyChunk_test_write() throws Exception {
		pageAssignmentsWriter.write(new Chunk<>());

		verifyNoInteractions(delegateWriter);
	}

	@Test
	void given_chunkWithEmptyLists_test_write() throws Exception {
		pageAssignmentsWriter.write(Chunk.of(List.of()));

		verifyNoInteractions(delegateWriter);
	}
}