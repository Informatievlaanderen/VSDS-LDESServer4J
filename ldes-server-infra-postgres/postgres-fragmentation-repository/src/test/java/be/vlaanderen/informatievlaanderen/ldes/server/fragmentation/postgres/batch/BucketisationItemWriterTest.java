package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.chunk.ChunkCollector;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.TreeRelation;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BucketisationItemWriterTest {
	private static final ViewName BY_TIME_VIEW_NAME = new ViewName("test", "by-time");
	private static final BucketDescriptorPair year2024Pair = new BucketDescriptorPair("year", "2024");
	private static final BucketDescriptorPair month09Pair = new BucketDescriptorPair("month", "09");
	private static final BucketDescriptorPair month08Pair = new BucketDescriptorPair("month", "08");
	private static final BucketDescriptorPair day23Pair = new BucketDescriptorPair("day", "23");
	private static final BucketDescriptorPair day26Pair = new BucketDescriptorPair("day", "26");

	@Mock
	private ItemWriter<Bucket> bucketItemWriter;
	@Mock
	private ItemWriter<Bucket> pageItemWriter;
	@Mock
	private ItemWriter<BucketisedMember> bucketMemberItemWriter;
	@Mock
	private ItemWriter<BucketRelation> bucketRelationItemWriter;
	private BucketisationItemWriter bucketisationItemWriter;

	@BeforeEach
	void setUp() {
		bucketisationItemWriter = new BucketisationItemWriter(bucketItemWriter, pageItemWriter, bucketMemberItemWriter, bucketRelationItemWriter);
	}

	@Test
	void test_ChunkOf4RootBuckets() throws Exception {
		AtomicInteger memberId = new AtomicInteger();
		final Chunk<Bucket> chunk = Stream.of(
						new BucketDescriptorPair[]{month09Pair, day23Pair},
						new BucketDescriptorPair[]{month09Pair, day26Pair},
						new BucketDescriptorPair[]{month08Pair, day26Pair},
						new BucketDescriptorPair[]{month08Pair, day23Pair}
				)
				.map(pair -> createBucketTree(pair[0], pair[1], memberId.incrementAndGet()))
				.collect(new ChunkCollector<>());

		bucketisationItemWriter.write(chunk);

		verify(bucketItemWriter, times(4)).write(assertArg(actual -> assertThat(actual).hasSize(4)));
		verify(pageItemWriter, times(4)).write(assertArg(actual -> assertThat(actual).hasSize(4)));
		verify(bucketRelationItemWriter, times(4)).write(assertArg(actual -> assertThat(actual).hasSize(3)));
		verify(bucketMemberItemWriter, times(4)).write(assertArg(actual -> assertThat(actual).hasSize(1)));
	}

	@Test
	void test_ChunkOf1RootBucket() throws Exception {
		final int memberId = 11;
		final Bucket rootBucket = createBucketTree(month09Pair, day23Pair, memberId);
		final List<Bucket> bucketTree = List.of(
				rootBucket,
				rootBucket.getChildren().getFirst(),
				rootBucket.getChildren().getFirst().getChildren().getFirst(),
				rootBucket.getChildren().getFirst().getChildren().getFirst().getChildren().getFirst()
		);
		final List<BucketRelation> relations = new ArrayList<>();
		for (int i = 1; i < bucketTree.size(); i++) {
			relations.add(new BucketRelation(
					bucketTree.get(i - 1).createPartialUrl(),
					bucketTree.get(i).createPartialUrl(),
					TreeRelation.generic()
			));

		}
		doAnswer(invocation -> {
			final Chunk<Bucket> buckets = (Chunk<Bucket>) invocation.getArgument(0, Chunk.class);
			List<Bucket> items = buckets.getItems();
			for (int i = 0; i < items.size(); i++) {
				Bucket item = items.get(i);
				assertThat(item.getBucketId()).isZero();
				item.setBucketId(i + 1);
			}
			return null;
		}).when(bucketItemWriter).write(any());

		bucketisationItemWriter.write(Chunk.of(rootBucket));

		verify(bucketItemWriter).write(assertArg(actual -> assertThat(actual.getItems())
				.asInstanceOf(InstanceOfAssertFactories.list(Bucket.class))
				.containsExactlyInAnyOrderElementsOf(bucketTree)
		));
		verify(pageItemWriter).write(assertArg(actual -> assertThat(actual.getItems())
				.asInstanceOf(InstanceOfAssertFactories.list(Bucket.class))
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(bucketTree)
				.noneMatch(bucket -> bucket.getBucketId() == 0)
		));
		verify(bucketRelationItemWriter).write(assertArg(actual -> assertThat(actual.getItems())
				.asInstanceOf(InstanceOfAssertFactories.list(BucketRelation.class))
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(relations)
		));
		verify(bucketMemberItemWriter).write(assertArg(actual -> assertThat(actual.getItems())
				.first()
				.isEqualTo(new BucketisedMember(bucketTree.size(), memberId))));
	}

	private static Bucket createBucketTree(BucketDescriptorPair monthPair, BucketDescriptorPair dayPair, long memberId) {
		final Bucket rootBucket = Bucket.createRootBucketForView(BY_TIME_VIEW_NAME);
		final Bucket yearBucket = addAndReturnChild(rootBucket, year2024Pair);
		final Bucket monthBucket = addAndReturnChild(yearBucket, monthPair);
		final Bucket dayBucket = addAndReturnChild(monthBucket, dayPair);
		dayBucket.assignMember(memberId);
		return rootBucket;
	}

	private static Bucket addAndReturnChild(Bucket bucket, BucketDescriptorPair bucketDescriptorPair) {
		return bucket.addChildBucket(bucket.createChild(bucketDescriptorPair).withGenericRelation());
	}
}