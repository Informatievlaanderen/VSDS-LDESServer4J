package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

@Component
public class PaginationCompactionService {

    private final CompactableFragmentPredicate compactableFragmentPredicate;
    private final FragmentRepository fragmentRepository;
    private final FragmentationStrategy fragmentationStrategy;
    private final ObservationRegistry observationRegistry;
    private final AllocationRepository allocationRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public PaginationCompactionService(FragmentRepository fragmentRepository, @Qualifier("compaction-fragmentation") FragmentationStrategy fragmentationStrategy, ObservationRegistry observationRegistry, AllocationRepository allocationRepository, MemberRepository memberRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.fragmentRepository = fragmentRepository;
        this.fragmentationStrategy = fragmentationStrategy;
        this.observationRegistry = observationRegistry;
        this.allocationRepository = allocationRepository;
        this.memberRepository = memberRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.compactableFragmentPredicate = new CompactableFragmentPredicate(this.fragmentRepository);
    }

    public void applyCompactionStartingFromNode(Fragment fragment) {
        Fragment currentFragment = fragment;
        while (!currentFragment.getRelations().isEmpty()) {
            Optional<Fragment> fragment2 = fragmentRepository.retrieveFragment(currentFragment.getRelations().stream().map(TreeRelation::treeNode).max(new CompactionComparator()).orElse(currentFragment.getRelations().get(0).treeNode()));
            if (fragment2.isPresent()) {
                if (currentFragment.getRelations().size() == 1 && compactableFragmentPredicate.test(fragment2.get())) {
                    compactFragments(currentFragment, fragment2.get());
                }
                currentFragment = fragment2.get();
            } else {
                break;
            }
        }
    }

    private void compactFragments(Fragment firstFragment, Fragment secondFragment) {
        List<FragmentPair> fragmentPairs = new ArrayList<>(firstFragment.getFragmentPairs());
        fragmentPairs.remove(fragmentPairs.size() - 1);
        fragmentPairs.add(new FragmentPair("pageNumber", getPageNumber(firstFragment) + "/" + getPageNumber(secondFragment)));
        LdesFragmentIdentifier ldesFragmentIdentifier = new LdesFragmentIdentifier(firstFragment.getViewName(), fragmentPairs);
        if (fragmentRepository.retrieveFragment(ldesFragmentIdentifier).isEmpty()) {
            Fragment fragment = new Fragment(ldesFragmentIdentifier, true, 0, secondFragment.getRelations());
            List<MemberAllocation> memberAllocationsByFragmentIdOne = allocationRepository.getMemberAllocationsByFragmentId(firstFragment.getFragmentIdString());
            List<MemberAllocation> memberAllocationsByFragmentIdTwo = allocationRepository.getMemberAllocationsByFragmentId(secondFragment.getFragmentIdString());
            List<String> memberIds = Stream.of(memberAllocationsByFragmentIdOne, memberAllocationsByFragmentIdTwo)
                    .flatMap(List::stream)
                    .map(MemberAllocation::getMemberId).toList();
            fragmentRepository.saveFragment(fragment);
            List<Fragment> fragments = fragmentRepository.retrieveFragmentsByOutgoingRelation(firstFragment.getFragmentId());
            fragments.forEach(prefragment -> {
                prefragment.addRelation(new TreeRelation("", fragment.getFragmentId(), "", "", GENERIC_TREE_RELATION));
                fragmentRepository.saveFragment(prefragment);
            });
            memberRepository.findAllByIds(memberIds).forEach(member -> {
                Observation compactionObservation = Observation.createNotStarted("compaction", observationRegistry).start();
                fragmentationStrategy.addMemberToFragment(fragment, member.getId(), member.getModel(), compactionObservation);
                compactionObservation.stop();
            });
            applicationEventPublisher.publishEvent(new FragmentsCompactedEvent(firstFragment, secondFragment));
        }

    }

    private String getPageNumber(Fragment firstFragment) {
        return firstFragment.getFragmentPairs().get(firstFragment.getFragmentPairs().size() - 1).fragmentValue();
    }
}
