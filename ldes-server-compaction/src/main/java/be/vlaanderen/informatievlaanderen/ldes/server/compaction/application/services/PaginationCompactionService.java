package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.CompactedFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRelationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.PageMemberRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class PaginationCompactionService {
	private final PageRelationRepository pageRelationRepository;
	private final PageMemberRepository pageMemberRepository;
	private final CompactedFragmentCreator compactedFragmentCreator;
	private final PageDeletionTimeSetter pageDeletionTimeSetter;
	private final ObservationRegistry observationRegistry;

	public PaginationCompactionService(PageRelationRepository pageRelationRepository, PageMemberRepository pageMemberRepository,
                                       CompactedFragmentCreator compactedFragmentCreator, PageDeletionTimeSetter pageDeletionTimeSetter, ObservationRegistry observationRegistry) {
        this.pageRelationRepository = pageRelationRepository;
        this.pageMemberRepository = pageMemberRepository;
        this.compactedFragmentCreator = compactedFragmentCreator;
        this.pageDeletionTimeSetter = pageDeletionTimeSetter;
        this.observationRegistry = observationRegistry;
	}

	public void applyCompactionForFragments(Set<CompactionCandidate> toBeCompactedFragments) {
		Observation compactionObservation = Observation.createNotStarted("compaction", observationRegistry).start();

		long compactedFragmentId = compactedFragmentCreator.createCompactedPage(toBeCompactedFragments);
		List<Long> compactedPageIds = toBeCompactedFragments.stream().map(CompactionCandidate::getId).toList();

		pageMemberRepository.setPageMembersToNewPage(compactedFragmentId, compactedPageIds);
		pageRelationRepository.updateCompactionBucketRelations(compactedPageIds, compactedFragmentId);
		pageDeletionTimeSetter.setDeleteTimeOfFragment(compactedPageIds);

		compactionObservation.stop();
	}
}
