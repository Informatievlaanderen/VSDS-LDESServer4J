package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.PostgresFragmentationIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class BucketisedMemberWriterTest extends PostgresFragmentationIntegrationTest {
	@Autowired
	BucketisedMemberWriter writer;
	@Autowired
	DataSource dataSource;

	@Test
	@Sql("./init-writer-test.sql")
	void testWriter() throws Exception {
		List<BucketisedMember> bucketisedMembers = initBucketisedMembers();
		writer.write(Chunk.of(List.of(bucketisedMembers.get(0), bucketisedMembers.get(1)),
				List.of(bucketisedMembers.get(2))));

		var count = new JdbcTemplate(dataSource).queryForObject("SELECT COUNT(*) FROM page_members", Integer.class);

		assertThat(count).isEqualTo(3);

	}

	private static List<BucketisedMember> initBucketisedMembers() {
		return IntStream.range(1, 4)
				.mapToObj(id -> new BucketisedMember(1, id))
				.toList();
	}
}
