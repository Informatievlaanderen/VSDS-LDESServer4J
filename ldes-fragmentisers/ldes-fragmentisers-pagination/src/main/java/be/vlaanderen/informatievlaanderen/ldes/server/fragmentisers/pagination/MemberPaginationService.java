package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.OpenPageProvider;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.stream.Stream;

public class MemberPaginationService {
    private final FragmentSequenceRepository fragmentSequenceRepository;
    private final BucketisedMemberRepository bucketisedMemberRepository;
    private final OpenPageProvider openPageProvider;
    private final FragmentRepository fragmentRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ViewName viewName;
    private long sequenceNr;
    private boolean isRunning;

    public MemberPaginationService(FragmentSequenceRepository fragmentSequenceRepository, BucketisedMemberRepository bucketisedMemberRepository,
                                   OpenPageProvider openPageProvider, FragmentRepository fragmentRepository, ApplicationEventPublisher eventPublisher, ViewName viewName) {
        this.fragmentSequenceRepository = fragmentSequenceRepository;
        this.bucketisedMemberRepository = bucketisedMemberRepository;
        this.openPageProvider = openPageProvider;
        this.fragmentRepository = fragmentRepository;
        this.eventPublisher = eventPublisher;
        this.viewName = viewName;
        isRunning = false;
        sequenceNr = determineLastProcessedSequence(viewName).sequenceNr();
    }

    public void paginateMember() {
        isRunning = true;

        List<BucketisedMember> bucketisedMembers = getNextMember(viewName);
        while(!bucketisedMembers.isEmpty()) {
            List<ImmutablePair<Fragment, BucketisedMember>> pages = bucketisedMembers.stream().map(member -> {
                Fragment page = openPageProvider
                        .retrieveOpenFragmentOrCreateNewFragment(LdesFragmentIdentifier.fromFragmentId(member.fragmentId()));
                return new ImmutablePair<>(page, member);
            }).toList();

            pages.forEach(pair -> {
                BucketisedMember member = pair.getRight();
                Fragment page = pair.getLeft();
                eventPublisher.publishEvent(new MemberAllocatedEvent(member.memberId(), page.getViewName().getCollectionName(),
                        page.getViewName().asString(), page.getFragmentId().asDecodedFragmentId()));
            });

            pages.forEach(pair -> {
                Fragment page = pair.getLeft();
                fragmentRepository.incrementNrOfMembersAdded(page.getFragmentId());
            });
            fragmentSequenceRepository.saveLastProcessedSequence(new FragmentSequence(viewName, sequenceNr + 1));
            sequenceNr += 1;
            bucketisedMembers = getNextMember(viewName);
        }
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private FragmentSequence determineLastProcessedSequence(ViewName viewName) {
        return fragmentSequenceRepository.findLastProcessedSequence(viewName)
                .orElse(FragmentSequence.createNeverProcessedSequence(viewName));
    }

    private List<BucketisedMember> getNextMember(ViewName viewName) {
        return bucketisedMemberRepository.getFirstUnallocatedMember(viewName, sequenceNr + 1);
    }
}
