package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.AllocationAggregate;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentComparator;

import java.util.Comparator;

public class CompactionCandidateComparator implements Comparator<AllocationAggregate> {
	FragmentComparator comparator = new FragmentComparator();

	@Override
	public int compare(AllocationAggregate ag1, AllocationAggregate ag2) {
		return comparator.compare(ag1.getFragment(), ag2.getFragment());
	}

}
