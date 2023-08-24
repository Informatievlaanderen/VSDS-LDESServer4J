package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;

public class CompactionComparator implements java.util.Comparator<LdesFragmentIdentifier> {
	@Override
	public int compare(LdesFragmentIdentifier o1, LdesFragmentIdentifier o2) {
		int length = getCompactionLength(o1);
		int length2 = getCompactionLength(o2);
		return Integer.compare(length, length2);
	}

	private static int getCompactionLength(LdesFragmentIdentifier o1) {
		String pageNumber = o1.getValueOfFragmentPairKey("pageNumber").orElse("");
		return pageNumber.split("/").length;
	}
}
