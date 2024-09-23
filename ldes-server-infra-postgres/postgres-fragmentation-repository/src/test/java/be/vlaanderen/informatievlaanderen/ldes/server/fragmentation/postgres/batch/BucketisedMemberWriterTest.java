package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.PostgresBucketisationIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.chunk.ChunkCollector;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
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
		final long bucketId = 3;
		final Chunk<BucketisedMember> members = IntStream.range(1, 4)
				.mapToObj(memberId -> new BucketisedMember(bucketId, memberId))
				.collect(new ChunkCollector<>());

		writer.write(members);

		var count = new JdbcTemplate(dataSource).queryForObject("SELECT COUNT(*) FROM page_members", Integer.class);
		assertThat(count).isEqualTo(3);

	}
}
