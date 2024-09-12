package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.bucketising.ReferenceBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceBucketCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.ReferenceFragmentationStrategyWrapper.DEFAULT_FRAGMENTATION_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceBucketCreator.FRAGMENT_KEY_REFERENCE_ROOT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReferenceFragmentationStrategyTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final Bucket PARENT_BUCKET = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
	private static final Bucket ROOT_TILE_BUCKET = PARENT_BUCKET.createChild(new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, FRAGMENT_KEY_REFERENCE_ROOT));

	private ReferenceBucketiser referenceBucketiser;
	private ReferenceBucketCreator bucketCreator;
	private FragmentationStrategy decoratedFragmentationStrategy;
	private ReferenceFragmentationStrategy referenceFragmentationStrategy;

	@BeforeEach
	void setUp() {
		referenceBucketiser = mock(ReferenceBucketiser.class);
		bucketCreator = mock(ReferenceBucketCreator.class);
		decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
		when(bucketCreator.getOrCreateRootBucket(PARENT_BUCKET, FRAGMENT_KEY_REFERENCE_ROOT))
				.thenReturn(ROOT_TILE_BUCKET);
		referenceFragmentationStrategy = new ReferenceFragmentationStrategy(decoratedFragmentationStrategy,
				referenceBucketiser, bucketCreator, ObservationRegistry.create(), mock());
	}

	@Test
	void when_MemberIsAddedToFragment_ThenReferenceFragmentationIsApplied() {
		FragmentationMember member = mock(FragmentationMember.class);

		final var typePerceel = "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel";
		final var typeGebouw = "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Gebouw";
		final var typeAdres = "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Adres";

		when(referenceBucketiser.bucketise(member.getSubject(), member.getVersionModel()))
				.thenReturn(Set.of(typePerceel, typeGebouw, typeAdres));
		Bucket referenceBucketOne = mockCreationReferenceBucket(typePerceel);
		Bucket referenceBucketTwo = mockCreationReferenceBucket(typeGebouw);
		Bucket referenceBucketThree = mockCreationReferenceBucket(typeAdres);

		referenceFragmentationStrategy
				.addMemberToBucketAndReturnMembers(PARENT_BUCKET, member, mock(Observation.class));

		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToBucketAndReturnMembers(eq(referenceBucketOne),
				any(), any(Observation.class));
		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToBucketAndReturnMembers(eq(referenceBucketTwo),
				any(), any(Observation.class));
		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToBucketAndReturnMembers(eq(referenceBucketThree),
				any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}

	@Test
	void when_MemberIsAddedToBucket_ThenReferenceFragmentationIsApplied() {
		FragmentationMember member = mock(FragmentationMember.class);

		final var typeParcel = "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel";
		final var typeBuilding = "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Gebouw";
		final var typeAddress = "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Adres";

		when(referenceBucketiser.bucketise(member.getSubject(), member.getVersionModel()))
				.thenReturn(Set.of(typeParcel, typeBuilding, typeAddress));
		Bucket referenceBucketOne = mockCreationReferenceBucket(typeParcel);
		Bucket referenceBucketTwo = mockCreationReferenceBucket(typeBuilding);
		Bucket referenceBucketThree = mockCreationReferenceBucket(typeAddress);

		referenceFragmentationStrategy
				.addMemberToBucketAndReturnMembers(PARENT_BUCKET, member, mock(Observation.class));

		verify(decoratedFragmentationStrategy).addMemberToBucketAndReturnMembers(eq(referenceBucketOne), any(), any(Observation.class));
		verify(decoratedFragmentationStrategy).addMemberToBucketAndReturnMembers(eq(referenceBucketTwo), any(), any(Observation.class));
		verify(decoratedFragmentationStrategy).addMemberToBucketAndReturnMembers(eq(referenceBucketThree), any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}

	private Bucket mockCreationReferenceBucket(String tile) {
		Bucket referenceBucket = PARENT_BUCKET.createChild(new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, tile));
		when(bucketCreator.getOrCreateBucket(PARENT_BUCKET, tile, ROOT_TILE_BUCKET))
				.thenReturn(referenceBucket);
		return referenceBucket;
	}

}