package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.bucketising.ReferenceBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceBucketCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collection;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceBucketCreator.FRAGMENT_KEY_REFERENCE_ROOT;

public class ReferenceFragmentationStrategy extends FragmentationStrategyDecorator {

    public static final String REFERENCE_FRAGMENTATION = "ReferenceFragmentation";

    private final ReferenceBucketiser referenceBucketiser;
    private final ReferenceBucketCreator bucketCreator;
    private final ObservationRegistry observationRegistry;

    public ReferenceFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
                                          ReferenceBucketiser referenceBucketiser,
                                          ReferenceBucketCreator bucketCreator,
                                          ObservationRegistry observationRegistry,
                                          ApplicationEventPublisher applicationEventPublisher) {
        super(fragmentationStrategy, applicationEventPublisher);
        this.referenceBucketiser = referenceBucketiser;
        this.bucketCreator = bucketCreator;
        this.observationRegistry = observationRegistry;
    }

    @Override
    public List<BucketisedMember> addMemberToBucket(Bucket parentBucket, FragmentationMember member,
                                                    Observation parentObservation) {
        final var fragmentationObservation = startObservation(parentObservation);
        final var rootFragment = getOrCreateRootBucket(parentBucket);
        var fragments =
                referenceBucketiser
                        .bucketise(member.getSubject(), member.getVersionModel())
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

    private Bucket getOrCreateRootBucket(Bucket parentBucket) {
        Bucket referenceRootFragment = bucketCreator.getOrCreateRootBucket(parentBucket, FRAGMENT_KEY_REFERENCE_ROOT);
        super.addRelationFromParentToChild(parentBucket, referenceRootFragment);
        return referenceRootFragment;
    }

}
