package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;

import java.util.function.Predicate;

public class CompactableFragmentPredicate implements Predicate<Fragment> {

    private final FragmentRepository fragmentRepository;

    public CompactableFragmentPredicate(FragmentRepository fragmentRepository) {
        this.fragmentRepository = fragmentRepository;
    }

    @Override
    public boolean test(Fragment fragment) {
        return fragment.getNumberOfMembers() > 0 && fragment.isImmutable() && fragmentRepository.retrieveFragmentsByOutgoingRelation(fragment.getFragmentId()).size() == 1;
    }
}
