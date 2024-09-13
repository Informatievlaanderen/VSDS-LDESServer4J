package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.bucketising.ReferenceBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceBucketCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

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
	                                      ObservationRegistry observationRegistry) {
		super(fragmentationStrategy);
		this.referenceBucketiser = referenceBucketiser;
		this.bucketCreator = bucketCreator;
		this.observationRegistry = observationRegistry;
	}

    @Override
    public List<BucketisedMember> addMemberToBucketAndReturnMembers(Bucket parentBucket, FragmentationMember member,
                                                                    Observation parentObservation) {
        final var fragmentationObservation = startObservation(parentObservation);
        final var rootFragment = getOrCreateRootBucket(parentBucket);
        var fragments =
                referenceBucketiser
                        .createReferences(member.getSubject(), member.getVersionModel())
                        .stream()
                        .map(reference -> bucketCreator.getOrCreateBucket(parentBucket, reference, rootFragment))
                        .toList();

        List<BucketisedMember> members = fragments.parallelStream()
                .map(bucket -> super.addMemberToBucketAndReturnMembers(bucket, member, fragmentationObservation))
                .flatMap(Collection::stream)
                .toList();
        fragmentationObservation.stop();
        return members;
    }

	@Override
	public void addMemberToBucket(Bucket parentBucket, FragmentationMember member, Observation parentObservation) {
		final var fragmentationObservation = startObservation(parentObservation);
		final var rootFragment = getOrCreateRootBucket(parentBucket);

		referenceBucketiser
				.createReferences(member.getSubject(), member.getVersionModel())
				.stream()
				.map(reference -> bucketCreator.getOrCreateBucket(parentBucket, reference, rootFragment))
				.parallel()
				.forEach(bucket -> super.addMemberToBucket(bucket, member, fragmentationObservation));

		fragmentationObservation.stop();
	}

	private Observation startObservation(Observation parentObservation) {
		return Observation.createNotStarted("reference fragmentation", observationRegistry)
				.parentObservation(parentObservation)
				.start();
	}

	private Bucket getOrCreateRootBucket(Bucket parentBucket) {
		Bucket referenceRootFragment = bucketCreator.getOrCreateRootBucket(parentBucket, FRAGMENT_KEY_REFERENCE_ROOT);
		parentBucket.addChildBucket(referenceRootFragment.withGenericRelation());
		return referenceRootFragment;
	}

}
