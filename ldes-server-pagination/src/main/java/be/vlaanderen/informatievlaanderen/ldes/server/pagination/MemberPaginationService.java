package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.services.OpenPageProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PaginationSequenceRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.concurrent.Future;

public class MemberPaginationService {
    private final PaginationSequenceRepository sequenceRepository;
    private final BucketisedMemberRepository bucketisedMemberRepository;
    private final OpenPageProvider openPageProvider;
    private final FragmentRepository fragmentRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ViewName viewName;
    private long sequenceNr;
    private Future<?> task;

    public MemberPaginationService(PaginationSequenceRepository sequenceRepository, BucketisedMemberRepository bucketisedMemberRepository,
                                   OpenPageProvider openPageProvider, FragmentRepository fragmentRepository, ApplicationEventPublisher eventPublisher, ViewName viewName) {
        this.sequenceRepository = sequenceRepository;
        this.bucketisedMemberRepository = bucketisedMemberRepository;
        this.openPageProvider = openPageProvider;
        this.fragmentRepository = fragmentRepository;
        this.eventPublisher = eventPublisher;
        this.viewName = viewName;
        sequenceNr = determineLastProcessedSequence(viewName).sequenceNr();
    }

    public void paginateMember() {
        List<BucketisedMember> bucketisedMembers = getNextMember(viewName);
        while(!bucketisedMembers.isEmpty()) {
            List<ImmutablePair<Fragment, BucketisedMember>> pages = getPages(bucketisedMembers);
            allocateMembers(pages);

            sequenceRepository.saveLastProcessedSequence(new FragmentSequence(viewName, ++sequenceNr));
            bucketisedMembers = getNextMember(viewName);
        }
    }

    public boolean isRunning() {
        return task != null && !(task.isDone() || task.isCancelled());
    }

    public void stopTask() {
        task.cancel(true);
    }

    public void setTask(Future<?> task) {
        this.task = task;
    }

    private FragmentSequence determineLastProcessedSequence(ViewName viewName) {
        return sequenceRepository.findLastProcessedSequence(viewName)
                .orElse(new FragmentSequence(viewName, 0));
    }

    private List<BucketisedMember> getNextMember(ViewName viewName) {
        return bucketisedMemberRepository.getFirstUnallocatedMember(viewName, sequenceNr + 1);
    }

    private List<ImmutablePair<Fragment, BucketisedMember>> getPages(List<BucketisedMember> bucketisedMembers) {
        return bucketisedMembers.stream().map(member -> {
            Fragment page = openPageProvider
                    .retrieveOpenFragmentOrCreateNewFragment(LdesFragmentIdentifier.fromFragmentId(member.fragmentId()));
            return new ImmutablePair<>(page, member);
        }).toList();
    }

    private void allocateMembers(List<ImmutablePair<Fragment, BucketisedMember>> pages) {
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
    }
}
