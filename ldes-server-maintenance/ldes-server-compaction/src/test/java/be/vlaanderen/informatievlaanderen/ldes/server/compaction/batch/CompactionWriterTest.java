package be.vlaanderen.informatievlaanderen.ldes.server.compaction.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services.PageDeletionTimeSetter;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.CompactedFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.CompactionPageRelationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.repository.PageMemberRepository;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

class CompactionWriterTest {
	private CompactionPageRelationRepository pageRelationRepository;
	private PageMemberRepository pageMemberRepository;
	private CompactedFragmentCreator compactedFragmentCreator;
	private PageDeletionTimeSetter pageDeletionTimeSetter;
	private CompactionWriter compactionWriter;

	@BeforeEach
	void setUp() {
		pageRelationRepository = mock();
		pageMemberRepository = mock();
		compactedFragmentCreator = mock();
		pageDeletionTimeSetter = mock();
		compactionWriter = new CompactionWriter(pageRelationRepository, pageMemberRepository, compactedFragmentCreator, pageDeletionTimeSetter, ObservationRegistry.NOOP);
	}

	@Test
	void when_CompactPages_Then_PagesAreCompacted() {
		List<Long> ids = List.of(3L, 2L, 1L);
		long newId = 10L;
		final Set<CompactionCandidate> candidates = createCompactionCandidates();
		when(compactedFragmentCreator.createCompactedPage(candidates)).thenReturn(newId);

		compactionWriter.write(candidates);

		InOrder inOrder = inOrder(compactedFragmentCreator, pageMemberRepository, pageRelationRepository, pageDeletionTimeSetter);
		inOrder.verify(compactedFragmentCreator).createCompactedPage(candidates);
		inOrder.verify(pageMemberRepository).setPageMembersToNewPage(newId, ids);
		inOrder.verify(pageRelationRepository).updateCompactionBucketRelations(ids, newId);
		inOrder.verify(pageDeletionTimeSetter).setDeleteTimeOfFragment(ids);
		inOrder.verifyNoMoreInteractions();
	}

	private static Set<CompactionCandidate> createCompactionCandidates() {
		return new HashSet<>(Set.of(
				new CompactionCandidate(1L, 5, 2L, 1L, "http://example.com"),
				new CompactionCandidate(2L, 5, 3L, 1L, "http://example.com"),
				new CompactionCandidate(3L, 5, 4L, 1L, "http://example.com")
		));
	}
}