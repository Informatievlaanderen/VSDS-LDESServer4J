package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompactionCandidateServiceTest {
	PageRepository pagePostgresRepository = mock(PageRepository.class);
	CompactionCandidateService service = new CompactionCandidateService(pagePostgresRepository);

	List<CompactionCandidate> candidates = List.of(
			new CompactionCandidate(1L, 2, 2L, true, null, 1L, "/ex/p"),
			new CompactionCandidate(2L, 3, 3L, true, null, 1L, "/ex/p"),
			new CompactionCandidate(3L, 8, 4L, true, null, 1L, "/ex/p"),
			new CompactionCandidate(4L, 3, 5L, true, null, 1L, "/ex/p"),
			new CompactionCandidate(5L, 3, 6L, true, null, 1L, "/ex/p"),
			new CompactionCandidate(6L, 2, 7L, true, LocalDateTime.now(), 1L, "/ex/p"),
			new CompactionCandidate(7L, 2, 8L, false, null, 1L, "/ex/p")
	);

	@BeforeEach
	void setup() {
		when(pagePostgresRepository.getPossibleCompactionCandidates(any(), anyInt()))
				.thenReturn(candidates.stream());
	}

	@Test
	void testGetPossibleCompactionCandidates() {
		var compactionCandidates = service.getPossibleCompactionCandidates(
				new ViewCapacity(ViewName.fromString("ex/p"), 10));

		assertEquals(5, compactionCandidates.size());
		assertEquals(1L, compactionCandidates.get(0).getId());
		assertEquals(2L, compactionCandidates.get(1).getId());
		assertEquals(3L, compactionCandidates.get(2).getId());
		assertEquals(4L, compactionCandidates.get(3).getId());
		assertEquals(5L, compactionCandidates.get(4).getId());
	}

	@Test
	void testGetCompactionTaskList() {
		var taskList = service.getCompactionTaskList(new ViewCapacity(ViewName.fromString("c/v"), 10));

		assertEquals(2, taskList.size());

		var firstCompaction = taskList.stream().toList().get(0);
		assertEquals(2, firstCompaction.size());
		assertTrue(firstCompaction.stream().anyMatch(candidate -> candidate.getId() == 1L));
		assertTrue(firstCompaction.stream().anyMatch(candidate -> candidate.getId() == 2L));

		var secondCompaction = taskList.stream().toList().get(1);
		assertEquals(2, secondCompaction.size());
		assertTrue(secondCompaction.stream().anyMatch(candidate -> candidate.getId() == 4L));
		assertTrue(secondCompaction.stream().anyMatch(candidate -> candidate.getId() == 5L));
	}
}
