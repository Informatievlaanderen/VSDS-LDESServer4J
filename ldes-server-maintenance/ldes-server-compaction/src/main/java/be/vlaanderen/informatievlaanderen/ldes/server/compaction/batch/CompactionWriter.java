package be.vlaanderen.informatievlaanderen.ldes.server.compaction.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services.PageDeletionTimeSetter;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.CompactedFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.CompactionPageRelationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.repository.PageMemberRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class CompactionWriter {
	private final CompactionPageRelationRepository pageRelationRepository;
	private final PageMemberRepository pageMemberRepository;
	private final CompactedFragmentCreator compactedFragmentCreator;
	private final PageDeletionTimeSetter pageDeletionTimeSetter;
	private final ObservationRegistry observationRegistry;

	public CompactionWriter(CompactionPageRelationRepository pageRelationRepository,
	                        PageMemberRepository pageMemberRepository,
	                        CompactedFragmentCreator compactedFragmentCreator,
	                        PageDeletionTimeSetter pageDeletionTimeSetter,
	                        ObservationRegistry observationRegistry) {
		this.pageRelationRepository = pageRelationRepository;
		this.pageMemberRepository = pageMemberRepository;
		this.compactedFragmentCreator = compactedFragmentCreator;
		this.pageDeletionTimeSetter = pageDeletionTimeSetter;
		this.observationRegistry = observationRegistry;
	}

	public void write(Set<CompactionCandidate> toBeCompactedPages) {
		Observation compactionObservation = Observation.createNotStarted("compaction", observationRegistry).start();

		long compactedFragmentId = compactedFragmentCreator.createCompactedPage(toBeCompactedPages);
		List<Long> compactedPageIds = toBeCompactedPages.stream().map(CompactionCandidate::getId).toList();

		pageMemberRepository.setPageMembersToNewPage(compactedFragmentId, compactedPageIds);
		pageRelationRepository.updateCompactionBucketRelations(compactedPageIds, compactedFragmentId);
		pageDeletionTimeSetter.setDeleteTimeOfFragment(compactedPageIds);

		compactionObservation.stop();
	}
}
