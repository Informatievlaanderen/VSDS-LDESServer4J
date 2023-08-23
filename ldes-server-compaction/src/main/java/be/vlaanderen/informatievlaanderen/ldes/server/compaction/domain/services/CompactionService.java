package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;


import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.ViewCollection;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

@Service
public class CompactionService {
    private final CompactableFragmentPredicate compactableFragmentPredicate = new CompactableFragmentPredicate();
    private final ViewCollection viewCollection;
    private final FragmentRepository fragmentRepository;
    private final FragmentationStrategy fragmentationStrategy;
    private final ObservationRegistry observationRegistry;
    private final AllocationRepository allocationRepository;
    private final MemberRepository memberRepository;

    public CompactionService(ViewCollection viewCollection, FragmentRepository fragmentRepository, @Qualifier("compaction-fragmentation") FragmentationStrategy fragmentationStrategy, ObservationRegistry observationRegistry, AllocationRepository allocationRepository, MemberRepository memberRepository) {
        this.viewCollection = viewCollection;
        this.fragmentRepository = fragmentRepository;
        this.fragmentationStrategy = fragmentationStrategy;
        this.observationRegistry = observationRegistry;
        this.allocationRepository = allocationRepository;
        this.memberRepository = memberRepository;
    }

    @Scheduled(fixedDelay = 10000)
    public void compactFragments() {
        viewCollection.getAllViewCapacities()
                .forEach(viewCapacity -> {
                    Optional<Fragment> fragment = fragmentRepository.retrieveRootFragment(viewCapacity.getViewName().asString());
                    if (fragment.isPresent()) {
                        List<Fragment> startingNodes = getStartingNodes(fragment.get());
                        startingNodes.forEach(fragment1 -> {
                            if (fragment1.getRelations().size() == 1) {
                                Optional<Fragment> fragment2 = fragmentRepository.retrieveFragment(fragment1.getRelations().get(0).treeNode());
                                if (fragment2.isPresent()) {
                                    if (compactableFragmentPredicate.test(fragment2.get())) {
                                        compactFragments(fragment1, fragment2.get());
                                    }
                                }
                            }
                            System.out.println(fragment1.getFragmentId().asString());
                        });
                    }
                });

    }

    private void compactFragments(Fragment firstFragment, Fragment secondFragment) {
        List<FragmentPair> fragmentPairs = new ArrayList<>(firstFragment.getFragmentPairs());
        fragmentPairs.remove(fragmentPairs.size() - 1);
        fragmentPairs.add(new FragmentPair("pageNumber", getPageNumber(firstFragment) + "/" + getPageNumber(secondFragment)));
        LdesFragmentIdentifier ldesFragmentIdentifier = new LdesFragmentIdentifier(firstFragment.getViewName(), fragmentPairs);
        Fragment fragment = new Fragment(ldesFragmentIdentifier, true, 0, secondFragment.getRelations());
        List<MemberAllocation> memberAllocationsByFragmentIdOne = allocationRepository.getMemberAllocationsByFragmentId(firstFragment.getFragmentIdString());
        List<MemberAllocation> memberAllocationsByFragmentIdTwo = allocationRepository.getMemberAllocationsByFragmentId(secondFragment.getFragmentIdString());
        List<String> memberIds = Stream.of(memberAllocationsByFragmentIdOne, memberAllocationsByFragmentIdTwo)
                .flatMap(List::stream)
                .map(MemberAllocation::getMemberId).toList();
        fragmentRepository.saveFragment(fragment);
        List<Fragment> fragments = fragmentRepository.retrieveFragmentsByOutgoingRelation(firstFragment.getFragmentId());
        fragments.forEach(prefragment->{
            prefragment.addRelation(new TreeRelation("",fragment.getFragmentId(),"","",GENERIC_TREE_RELATION));
            fragmentRepository.saveFragment(prefragment);
        });
        memberRepository.findAllByIds(memberIds).forEach(member -> {
            Observation compactionObservation = Observation.createNotStarted("compaction", observationRegistry).start();
            fragmentationStrategy.addMemberToFragment(fragment, member.getId(), member.getModel(), compactionObservation);
            compactionObservation.stop();
        });
    }

    private String getPageNumber(Fragment firstFragment) {
        return firstFragment.getFragmentPairs().get(firstFragment.getFragmentPairs().size() - 1).fragmentValue();
    }

    // Iterator
    private List<Fragment> getStartingNodes(Fragment fragment) {
        List<Fragment> startingNodes = new ArrayList<>();
        fragment.getRelations()
                .stream()
                .map(TreeRelation::treeNode)
                .map(fragmentRepository::retrieveFragment)
                .flatMap(Optional::stream)
                .forEach(relationFragment -> {
                    if (compactableFragmentPredicate.test(relationFragment)) {
                        startingNodes.add(relationFragment);
                    } else {
                        startingNodes.addAll(getStartingNodes(relationFragment));
                    }
                });
        return startingNodes;
    }
}
