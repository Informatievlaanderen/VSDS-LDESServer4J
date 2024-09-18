package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.PostgresBucketisationIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;

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

		var count = new JdbcTemplate(dataSource).queryForObject("SELECT COUNT(*) FROM page_members", Integer.class);

		assertThat(count).isEqualTo(3);

	}

	private static Bucket initBucket() {
		final ViewName byPageViewName = new ViewName("mobility-hindrances", "by-hour");
		final Bucket rootBucket = new Bucket(1, BucketDescriptor.empty(), byPageViewName);
		final Bucket yearBucket = new Bucket(2, BucketDescriptor.of(new BucketDescriptorPair("year", "2023")), byPageViewName);
		final Bucket monthBucket = new Bucket(3, BucketDescriptor.of(new BucketDescriptorPair("year", "2023"), new BucketDescriptorPair("month", "06")), byPageViewName);
		rootBucket.addChildBucket(yearBucket.withGenericRelation());
		yearBucket.addChildBucket(monthBucket.withGenericRelation());
		IntStream.range(1, 4).forEach(monthBucket::addMember);
		return rootBucket;
	}
}
