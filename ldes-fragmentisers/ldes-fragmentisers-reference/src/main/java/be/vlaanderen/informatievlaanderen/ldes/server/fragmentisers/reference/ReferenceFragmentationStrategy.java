package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.bucketising.ReferenceBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceFragmentCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.util.Collection;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceFragmentCreator.FRAGMENT_KEY_REFERENCE_ROOT;

public class ReferenceFragmentationStrategy extends FragmentationStrategyDecorator {

    public static final String REFERENCE_FRAGMENTATION = "ReferenceFragmentation";

    private final ReferenceBucketiser referenceBucketiser;
    private final ReferenceFragmentCreator fragmentCreator;
    private final ObservationRegistry observationRegistry;

    public ReferenceFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
                                          ReferenceBucketiser referenceBucketiser,
                                          ReferenceFragmentCreator fragmentCreator,
                                          ObservationRegistry observationRegistry,
                                          FragmentRepository fragmentRepository) {
        super(fragmentationStrategy, fragmentRepository);
        this.referenceBucketiser = referenceBucketiser;
        this.fragmentCreator = fragmentCreator;
        this.observationRegistry = observationRegistry;
    }

    @Override
    public List<BucketisedMember> addMemberToFragment(Fragment parentFragment, Member member,
                                                      Observation parentObservation) {
        final var fragmentationObservation = startObservation(parentObservation);
        final var rootFragment = getOrCreateRootFragment(parentFragment);
        var fragments =
                referenceBucketiser
                        .bucketise(member.id(), member.model())
                        .stream()
                        .map(reference -> fragmentCreator.getOrCreateFragment(parentFragment, reference, rootFragment))
                        .toList();

        List<BucketisedMember> members = fragments.parallelStream()
                .map(ldesFragment -> super.addMemberToFragment(ldesFragment, member, fragmentationObservation))
                .flatMap(Collection::stream)
                .toList();
        fragmentationObservation.stop();
        return members;
    }

    private Observation startObservation(Observation parentObservation) {
        return Observation.createNotStarted("reference fragmentation", observationRegistry)
                .parentObservation(parentObservation)
                .start();
    }

    private Fragment getOrCreateRootFragment(Fragment parentFragment) {
        Fragment referenceRootFragment = fragmentCreator.getOrCreateRootFragment(parentFragment, FRAGMENT_KEY_REFERENCE_ROOT);
        super.addRelationFromParentToChild(parentFragment, referenceRootFragment);
        return referenceRootFragment;
    }

}
