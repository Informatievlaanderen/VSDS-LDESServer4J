package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.delegates;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.PostgresBucketisationIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BucketPageItemWriterTest extends PostgresBucketisationIntegrationTest {
	private static final ViewName VIEW_NAME = new ViewName("mobility-hindrances", "by-hour");
	@Autowired
	private ItemWriter<Bucket> pageItemWriter;
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Test
	@Sql({"./init-collection-and-view.sql", "./init-writer-test.sql"})
	void testWriter() throws Exception {
		final BucketDescriptorPair[] pairs = BucketDescriptor.fromString("year=2023&month=06").getDescriptorPairs().toArray(new BucketDescriptorPair[0]);
		final List<Bucket> bucketTree = new TestBucketSupplier(VIEW_NAME, pairs, 12, true).getBucketTree();
		final Chunk<Bucket> bucketTreeChunk = new Chunk<>(bucketTree);

		pageItemWriter.write(bucketTreeChunk);

		var count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pages", Integer.class);
		assertThat(count).isEqualTo(3);
	}
}