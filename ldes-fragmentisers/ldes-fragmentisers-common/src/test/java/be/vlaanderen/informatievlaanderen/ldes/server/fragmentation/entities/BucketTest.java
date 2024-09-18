package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BucketTest {
	private static final String COLLECTION = "mobility-hindrances";
	private static final String VIEW = "by-time";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION, VIEW);
	private static final BucketDescriptorPair PARENT_DESCRIPTOR_PAIR = new BucketDescriptorPair("parent", "yes");
	private static final BucketDescriptorPair CHILD_DESCRIPTOR_PAIR = new BucketDescriptorPair("orphan", "no");

	@Test
	void test_GetDescriptor() {
		Bucket bucket = new Bucket(BucketDescriptor.of(PARENT_DESCRIPTOR_PAIR, CHILD_DESCRIPTOR_PAIR), VIEW_NAME);

		assertThat(bucket.getBucketDescriptorAsString()).isEqualTo("parent=yes&orphan=no");
	}

	@Test
	void test_ChildCreation() {
		final Bucket expectedBucket = new Bucket(BucketDescriptor.of(PARENT_DESCRIPTOR_PAIR, CHILD_DESCRIPTOR_PAIR), VIEW_NAME);
		final Bucket parentBucket = new Bucket(BucketDescriptor.of(PARENT_DESCRIPTOR_PAIR), VIEW_NAME);
		final Bucket childBucket = parentBucket.createChild(CHILD_DESCRIPTOR_PAIR);

		assertThat(childBucket).isEqualTo(expectedBucket);
	}

	@Test
	void testEquals() {
		Bucket equalBucket1 = new Bucket(BucketDescriptor.of(PARENT_DESCRIPTOR_PAIR, CHILD_DESCRIPTOR_PAIR), VIEW_NAME);
		Bucket equalBucket2 = new Bucket(new BucketDescriptor(List.of(PARENT_DESCRIPTOR_PAIR, CHILD_DESCRIPTOR_PAIR)), VIEW_NAME);
		Bucket otherBucketFromSameView = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
		Bucket otherBucket = new Bucket(BucketDescriptor.of(PARENT_DESCRIPTOR_PAIR, CHILD_DESCRIPTOR_PAIR), new ViewName("other", "other"));

		assertThat(equalBucket1)
				.isEqualTo(equalBucket1)
				.isEqualTo(equalBucket2)
				.isNotEqualTo(otherBucketFromSameView)
				.isNotEqualTo(otherBucket);
		assertThat(equalBucket2)
				.isEqualTo(equalBucket1)
				.isNotEqualTo(otherBucket);
		assertThat(otherBucketFromSameView).isNotEqualTo(otherBucket);
	}

	@Test
	void testHashCode() {
		Bucket equalBucket1 = new Bucket(BucketDescriptor.of(PARENT_DESCRIPTOR_PAIR, CHILD_DESCRIPTOR_PAIR), VIEW_NAME);
		Bucket equalBucket2 = new Bucket(new BucketDescriptor(List.of(PARENT_DESCRIPTOR_PAIR, CHILD_DESCRIPTOR_PAIR)), VIEW_NAME);
		Bucket otherBucketFromSameView = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
		Bucket otherBucket = new Bucket(BucketDescriptor.of(PARENT_DESCRIPTOR_PAIR, CHILD_DESCRIPTOR_PAIR), new ViewName("other", "other"));


		assertThat(equalBucket1.hashCode())
				.isEqualTo(equalBucket1.hashCode())
				.isEqualTo(equalBucket2.hashCode())
				.isNotEqualTo(otherBucketFromSameView.hashCode())
				.isNotEqualTo(otherBucket.hashCode());
		assertThat(equalBucket2.hashCode())
				.isEqualTo(equalBucket1.hashCode())
				.isNotEqualTo(otherBucket.hashCode());
		assertThat(otherBucketFromSameView.hashCode())
				.isNotEqualTo(otherBucket.hashCode());
	}

	@Nested
	class CompositionTest {
		private static final int DAY_14_BUCKET_ID = 10;
		private static final int DAY_28_BUCKET_ID = 11;
		private Bucket rootBucket, year2023Bucket, year2024Bucket, month01Bucket, month03Bucket, day14Bucket, day28Bucket;

		@BeforeEach
		void setUp() {
			rootBucket = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
			year2023Bucket = rootBucket.createChild(new BucketDescriptorPair("year", "2023"));
			year2024Bucket = rootBucket.createChild(new BucketDescriptorPair("year", "2024"));
			month01Bucket = year2024Bucket.createChild(new BucketDescriptorPair("month", "01"));
			month03Bucket = year2024Bucket.createChild(new BucketDescriptorPair("month", "03"));
			day14Bucket = new Bucket(DAY_14_BUCKET_ID, month03Bucket.createChild(new BucketDescriptorPair("day", "14")).getBucketDescriptor(), VIEW_NAME);
			day28Bucket = new Bucket(DAY_28_BUCKET_ID, month03Bucket.createChild(new BucketDescriptorPair("day", "28")).getBucketDescriptor(), VIEW_NAME);
			month03Bucket.addChildBucket(day14Bucket.withGenericRelation());
			month03Bucket.addChildBucket(day28Bucket.withGenericRelation());
			year2024Bucket.addChildBucket(month01Bucket.withGenericRelation());
			year2024Bucket.addChildBucket(month03Bucket.withGenericRelation());
			rootBucket.addChildBucket(year2023Bucket.withGenericRelation());
			rootBucket.addChildBucket(year2024Bucket.withGenericRelation());
		}

		@Test
		void test_GetBucketTree() {
			final List<Bucket> descendants = rootBucket.getBucketTree();

			assertThat(descendants).containsExactlyInAnyOrder(
					rootBucket, year2023Bucket, year2024Bucket, month01Bucket, month03Bucket, day14Bucket, day28Bucket
			);
		}
	}
}