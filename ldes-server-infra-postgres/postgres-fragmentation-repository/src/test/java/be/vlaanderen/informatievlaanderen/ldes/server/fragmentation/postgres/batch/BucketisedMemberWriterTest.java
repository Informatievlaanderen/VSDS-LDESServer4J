package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.ChildBucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.PostgresBucketisationIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.chunk.ChunkCollector;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class BucketisedMemberWriterTest extends PostgresBucketisationIntegrationTest {
	@Autowired
	ItemWriter<BucketisedMember> writer;
	@Autowired
	DataSource dataSource;

	@Test
	@Sql("./init-writer-test.sql")
	void testWriter() throws Exception {
		final Chunk<BucketisedMember> members = initRootBucket().getBucketTree().stream()
				.flatMap(bucket -> bucket.getBucketisedMembers().stream())
				.collect(new ChunkCollector<>());

		writer.write(members);

		var count = new JdbcTemplate(dataSource).queryForObject("SELECT COUNT(*) FROM page_members", Integer.class);
		assertThat(count).isEqualTo(3);

	}

	private static Bucket initRootBucket() {
		final ViewName byPageViewName = new ViewName("mobility-hindrances", "by-hour");
		final Bucket rootBucket = new Bucket(1, BucketDescriptor.empty(), byPageViewName, List.of(), List.of());
		final ChildBucket yearBucket = new Bucket(2, BucketDescriptor.of(new BucketDescriptorPair("year", "2023")), byPageViewName, List.of(), List.of()).withGenericRelation();
		final ChildBucket monthBucket = new Bucket(3, BucketDescriptor.of(new BucketDescriptorPair("year", "2023"), new BucketDescriptorPair("month", "06")), byPageViewName, List.of(), List.of()).withGenericRelation();
		rootBucket.addChildBucket(yearBucket);
		yearBucket.addChildBucket(monthBucket);
		IntStream.range(1, 4).forEach(monthBucket::addMember);
		return rootBucket;
	}
}
