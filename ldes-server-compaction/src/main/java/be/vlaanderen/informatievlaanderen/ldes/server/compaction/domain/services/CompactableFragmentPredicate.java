package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import java.util.function.Predicate;

public class CompactableFragmentPredicate implements Predicate<Fragment> {

    @Override
    public boolean test(Fragment fragment) {
        return fragment.getNumberOfMembers() > 0
                && fragment.isImmutable();
    }
}
