package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.delegates;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.PostgresBucketisationIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.chunk.ChunkCollector;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.TreeRelation;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PageRelationItemWriterTest extends PostgresBucketisationIntegrationTest {
	private static final ViewName VIEW_NAME = new ViewName("mobility-hindrances", "by-hour");

	@Autowired
	private ItemWriter<BucketRelation> relationItemWriter;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	@Sql({"./init-collection-and-view.sql", "./init-writer-test.sql", "./insert-pages.sql"})
	void testWriter() throws Exception {
		final BucketDescriptorPair[] pairs = BucketDescriptor.fromString("year=2023&month=06").getDescriptorPairs().toArray(new BucketDescriptorPair[0]);
		final Chunk<BucketRelation> chunk = new TestBucketSupplier(VIEW_NAME, pairs, 12, true).getBucketTree().stream()
				.flatMap(bucket -> bucket.getChildRelations().stream())
				.collect(new ChunkCollector<>());
		chunk.add(new BucketRelation("/mobility-hindrances/by-hour?year=2023", "/mobility-hindrances/by-hour?year=2023&month=07", TreeRelation.generic()));

		relationItemWriter.write(chunk);

		var count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM page_relations", Integer.class);
		assertThat(count).isEqualTo(3);
	}
}