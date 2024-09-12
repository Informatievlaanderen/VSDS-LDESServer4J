package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import io.micrometer.observation.Observation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FragmentationStrategyImplTest {
	private static final long BUCKET_ID = 2L;
	private static final long MEMBER_ID = 1L;
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private final FragmentationStrategyImpl fragmentationStrategy = new FragmentationStrategyImpl();

	@Test
	void when_memberIsAddedToBucket_FragmentationStrategyImplSavesUpdatedFragment() {
		BucketisedMember expectedBucketisedMember = new BucketisedMember(BUCKET_ID, MEMBER_ID);
		Bucket bucket = new Bucket(2, BucketDescriptor.empty(), VIEW_NAME);
		FragmentationMember member = mock(FragmentationMember.class);
		when(member.getMemberId()).thenReturn(MEMBER_ID);

		List<BucketisedMember> members = fragmentationStrategy.addMemberToBucketAndReturnMembers(bucket, member, mock(Observation.class));

		assertThat(members)
				.hasSize(1)
				.first()
				.usingRecursiveComparison()
				.isEqualTo(expectedBucketisedMember);
	}
}
