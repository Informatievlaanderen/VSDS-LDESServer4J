package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier.fromFragmentId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompactionCandidateServiceTest {
	AllocationRepository allocationRepository = mock(AllocationRepository.class);
	FragmentRepository fragmentRepository = mock(FragmentRepository.class);
	CompactionCandidateService service = new CompactionCandidateService(allocationRepository, fragmentRepository);

	Map<String, Fragment> fragments = Map.of("/c/f1",
			new Fragment(fromFragmentId("/c/f1"), true, 10, List.of(relationTo("/c/f2")), LocalDateTime.now()),
			"/c/f2",
			new Fragment(fromFragmentId("/c/f2"), true, 10, List.of(relationTo("/c/f3")), null),
			"/c/f3",
			new Fragment(fromFragmentId("/c/f3"), true, 10, List.of(relationTo("/c/f4")), null),
			"/c/f4",
			new Fragment(fromFragmentId("/c/f4"), true, 10, List.of(relationTo("/c/f5")), null),
			"/c/f5",
			new Fragment(fromFragmentId("/c/f5"), true, 10, List.of(relationTo("/c/f6")), null),
			"/c/f6",
			new Fragment(fromFragmentId("/c/f6"), false, 5, List.of(), null)
	);

	@BeforeEach
	void setup() {
		when(allocationRepository.getPossibleCompactionCandidates(any(), anyInt()))
				.thenReturn(Stream.of(newCandidate("/c/f1"), newCandidate("/c/f4"), newCandidate("/c/f2"),
						newCandidate("/c/f5"), newCandidate("/c/f3"), newCandidate("/c/f6")));

		ArgumentCaptor<LdesFragmentIdentifier> argument = ArgumentCaptor.forClass(LdesFragmentIdentifier.class);
		Mockito.when(fragmentRepository.retrieveFragment(argument.capture()))
				.thenAnswer(invocation -> Optional.of(fragments.get(argument.getValue().asDecodedFragmentId())));
	}

	@Test
	void testGetPossibleCompactionCandidates() {
		var compactionCandidates = service.getPossibleCompactionCandidates(
				new ViewCapacity(ViewName.fromString("c/v"), 10));

		assertEquals(4, compactionCandidates.size());
		assertEquals("/c/f2", compactionCandidates.get(0).getId());
		assertEquals("/c/f3", compactionCandidates.get(1).getId());
		assertEquals("/c/f4", compactionCandidates.get(2).getId());
		assertEquals("/c/f5", compactionCandidates.get(3).getId());
	}

	@Test
	void testGetCompactionTaskList() {
		var taskList = service.getCompactionTaskList(new ViewCapacity(ViewName.fromString("c/v"), 10));

		assertEquals(2, taskList.size());

		var firstCompaction = taskList.stream().toList().get(0);
		assertEquals(2, firstCompaction.size());
		assertTrue(firstCompaction.stream().anyMatch(candidate -> candidate.getId().equals("/c/f2")));
		assertTrue(firstCompaction.stream().anyMatch(candidate -> candidate.getId().equals("/c/f3")));

		var secondCompaction = taskList.stream().toList().get(1);
		assertEquals(2, secondCompaction.size());
		assertTrue(secondCompaction.stream().anyMatch(candidate -> candidate.getId().equals("/c/f4")));
		assertTrue(secondCompaction.stream().anyMatch(candidate -> candidate.getId().equals("/c/f5")));
	}

	CompactionCandidate newCandidate(String id) {
		return new CompactionCandidate(id, 4);
	}

	TreeRelation relationTo(String id) {
		return new TreeRelation(null, fromFragmentId(id), null, null, null);
	}
}
