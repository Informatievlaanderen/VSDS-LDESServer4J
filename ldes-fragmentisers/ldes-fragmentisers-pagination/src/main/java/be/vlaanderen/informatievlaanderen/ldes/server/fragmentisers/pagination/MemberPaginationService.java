package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;

public class MemberPaginationService {
    private final PaginationStrategy paginationStrategy;
    private final FragmentSequenceRepository fragmentSequenceRepository;
    private long sequenceNr;

    public MemberPaginationService(PaginationStrategy paginationStrategy, FragmentSequenceRepository fragmentSequenceRepository) {
        this.paginationStrategy = paginationStrategy;
        this.fragmentSequenceRepository = fragmentSequenceRepository;
    }


    public void paginateMember(BucketisedMember member) {

        paginationStrategy.addMemberToFragment(member.fragmentId());
        fragmentSequenceRepository.saveLastProcessedSequence();
    }

    public long getSequenceNr() {
        return sequenceNr;
    }
}
