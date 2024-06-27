package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.bucketising.ReferenceBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceBucketCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceFragmentCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collection;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceFragmentCreator.FRAGMENT_KEY_REFERENCE_ROOT;

public class ReferenceFragmentationStrategy extends FragmentationStrategyDecorator {

    public static final String REFERENCE_FRAGMENTATION = "ReferenceFragmentation";

    private final ReferenceBucketiser referenceBucketiser;
    private final ReferenceFragmentCreator fragmentCreator;
    private final ReferenceBucketCreator bucketCreator;
    private final ObservationRegistry observationRegistry;

    public ReferenceFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
                                          ReferenceBucketiser referenceBucketiser,
                                          ReferenceFragmentCreator fragmentCreator,
                                          ReferenceBucketCreator bucketCreator,
                                          ObservationRegistry observationRegistry,
                                          FragmentRepository fragmentRepository,
                                          ApplicationEventPublisher applicationEventPublisher) {
        super(fragmentationStrategy, fragmentRepository, applicationEventPublisher);
        this.referenceBucketiser = referenceBucketiser;
        this.fragmentCreator = fragmentCreator;
        this.bucketCreator = bucketCreator;
        this.observationRegistry = observationRegistry;
    }

    @Override
    public List<BucketisedMember> addMemberToFragment(Fragment parentFragment, FragmentationMember member,
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

    @Override
    public List<BucketisedMember> addMemberToBucket(Bucket parentBucket, FragmentationMember member,
                                                    Observation parentObservation) {
        final var fragmentationObservation = startObservation(parentObservation);
        final var rootFragment = getOrCreateRootBucket(parentBucket);
        var fragments =
                referenceBucketiser
                        .bucketise(member.id(), member.model())
                        .stream()
                        .map(reference -> bucketCreator.getOrCreateBucket(parentBucket, reference, rootFragment))
                        .toList();

        List<BucketisedMember> members = fragments.parallelStream()
                .map(bucket -> super.addMemberToBucket(bucket, member, fragmentationObservation))
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

    private Bucket getOrCreateRootBucket(Bucket parentBucket) {
        Bucket referenceRootFragment = bucketCreator.getOrCreateRootBucket(parentBucket, FRAGMENT_KEY_REFERENCE_ROOT);
        super.addRelationFromParentToChild(parentBucket, referenceRootFragment);
        return referenceRootFragment;
    }

}
