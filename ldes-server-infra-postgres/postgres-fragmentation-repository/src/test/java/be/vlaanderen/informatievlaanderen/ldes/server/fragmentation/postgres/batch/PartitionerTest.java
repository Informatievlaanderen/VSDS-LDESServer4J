package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.PostgresFragmentationIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketEntity;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableBatchProcessing
@SpringBatchTest
class PartitionerTest extends PostgresFragmentationIntegrationTest {
	@Autowired
	private BucketisationPartitioner partitioner;

	@Autowired
	private ViewBucketisationPartitioner viewPartitioner;

	List<MemberBucketEntity> buckets = List.of(
			new MemberBucketEntity("es/v1", "v1/x", "1", 0L),
			new MemberBucketEntity("es/v1", "v1/x", "2", 0L),
			new MemberBucketEntity("es/v2", "v2/x", "1", 0L),
			new MemberBucketEntity("es/v2", "v2/x", "2", 0L),
			new MemberBucketEntity("es/v2", "v2/y", "1", 0L),
			new MemberBucketEntity("es/v2", "v2/y", "2", 0L)
	);

	@Test
	@Sql({"./allocations.sql"})
	void testBucketiser() {
		memberBucketJpaRepository.saveAll(buckets);

		var partitions = partitioner.partition(0);

		assertEquals(2, partitions.size());
	}

	@Test
	@Sql({"./allocations.sql"})
	void testViewBucketiser() {
		memberBucketJpaRepository.saveAll(buckets);

		viewPartitioner.setViewName("es/v1");
		var partitions = viewPartitioner.partition(0);
		assertEquals(0, partitions.size());

		viewPartitioner.setViewName("es/v2");
		partitions = viewPartitioner.partition(0);
		assertEquals(2, partitions.size());
	}
}
