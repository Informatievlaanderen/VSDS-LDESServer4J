package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.PostgresFragmentationIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.MemberBucketEntityRepository;
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
	public static final String SUBJECT_PREFIX = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810400/%d";
	@Autowired
	BucketisedMemberWriter writer;

	@Autowired
	MemberBucketEntityRepository repository;
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
		return IntStream.range(0, 3)
				.mapToObj(id -> new BucketisedMember(id /* SUBJECT_PREFIX.formatted(600000 + id) */ , new ViewName("mobility-hindrances", "by-hour"), "year=2023&month=06"))
				.toList();
	}
}
