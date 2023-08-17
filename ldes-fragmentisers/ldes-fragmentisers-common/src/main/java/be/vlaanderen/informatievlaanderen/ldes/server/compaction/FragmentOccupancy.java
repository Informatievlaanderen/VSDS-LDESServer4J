package be.vlaanderen.informatievlaanderen.ldes.server.compaction;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;

public class FragmentOccupancy {
    private final LdesFragmentIdentifier ldesFragmentIdentifier;
    private final int memberCount;
    private final int maxMemberCount;

    public FragmentOccupancy(LdesFragmentIdentifier ldesFragmentIdentifier, int memberCount, int maxMemberCount) {
        this.ldesFragmentIdentifier = ldesFragmentIdentifier;
        this.memberCount = memberCount;
        this.maxMemberCount = maxMemberCount;
    }
}
