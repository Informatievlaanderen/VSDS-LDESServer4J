package be.vlaanderen.informatievlaanderen.ldes.server.compaction.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CompactionCandidateSorterTest {
	private final CompactionCandidateSorter service = new CompactionCandidateSorter();

	private static final List<CompactionCandidate> compactableCandidates = List.of(
			new CompactionCandidate(1L, 2, 2L, 1L, "/ex/p"),
			new CompactionCandidate(2L, 3, 3L, 1L, "/ex/p"),
			new CompactionCandidate(3L, 8, 4L, 1L, "/ex/p"),
			new CompactionCandidate(4L, 3, 5L, 1L, "/ex/p"),
			new CompactionCandidate(5L, 3, 6L, 1L, "/ex/p")
	);


	@Test
	void testGetCompactionTaskList() {
		final Collection<Set<CompactionCandidate>> taskList = service.getCompactionCandidateList(compactableCandidates, 10);

		assertThat(taskList)
				.hasSize(2)
				.allSatisfy(set -> assertThat(set).hasSize(2))
				.map(set -> set.stream().map(CompactionCandidate::getId).toList())
				.containsExactlyInAnyOrder(List.of(1L, 2L), List.of(4L, 5L));
	}
}