package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.delegates;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.PostgresBucketisationIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

class BucketItemWriterTest extends PostgresBucketisationIntegrationTest {
	private static final ViewName VIEW_NAME = new ViewName("mobility-hindrances", "by-hour");
	@Autowired
	private BucketItemWriter bucketItemWriter;
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Test
	@Sql("./init-collection-and-view.sql")
	void testWriter() {
		final BucketDescriptorPair[] pairs = BucketDescriptor.fromString("year=2024&month=09&day=14&hour=09").getDescriptorPairs().toArray(new BucketDescriptorPair[0]);
		final Bucket rootBucket = new TestBucketSupplier(VIEW_NAME, pairs, 12).get();
		final Chunk<Bucket> bucketTreeChunk = new Chunk<>(rootBucket.getBucketTree());

		bucketItemWriter.write(bucketTreeChunk);

		var count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM buckets", Integer.class);
		assertThat(count).isEqualTo(5);
		assertThat(bucketTreeChunk.getItems()).allSatisfy(bucket -> assertThat(bucket.getBucketId()).isNotZero());
	}
}