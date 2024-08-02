package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.CompactedFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.PageMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRelationRepository;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PaginationCompactionServiceTest {
    private final PageRelationRepository pageRelationRepository = mock(PageRelationRepository.class);
    private final PageMemberRepository pageMemberRepository = mock(PageMemberRepository.class);
    private final CompactedFragmentCreator compactedFragmentCreator = mock(CompactedFragmentCreator.class);
    private final ObservationRegistry observationRegistry = ObservationRegistry.create();
    private PaginationCompactionService paginationCompactionService;
    private Set<CompactionCandidate> candidates;

    @BeforeEach
    void setUp() {
        candidates = new HashSet<>();
        paginationCompactionService = new PaginationCompactionService(pageRelationRepository, pageMemberRepository,
                compactedFragmentCreator, observationRegistry);
    }

    @Test
    void when_CompactPages_Then_PagesAreCompacted() {
        candidates.add(new CompactionCandidate(1L, 5, 2L, true,
                null, 1L, "http://example.com"));
        candidates.add(new CompactionCandidate(2L, 5, 3L, true,
                null, 1L, "http://example.com"));
        candidates.add(new CompactionCandidate(3L, 5, 4L, true,
                null, 1L, "http://example.com"));
        when(compactedFragmentCreator.createCompactedPage(candidates)).thenReturn(10L);

        paginationCompactionService.applyCompactionForFragments(candidates);

        InOrder inOrder = inOrder(pageRelationRepository, pageMemberRepository,
                compactedFragmentCreator);
        inOrder.verify(compactedFragmentCreator).createCompactedPage(candidates);
        inOrder.verify(pageMemberRepository).setPageMembersToNewPage(10L, List.of(3L, 2L, 1L));
        inOrder.verify(pageRelationRepository).updateCompactionBucketRelations(List.of(3L, 2L, 1L), 10L);
        inOrder.verifyNoMoreInteractions();
    }
}