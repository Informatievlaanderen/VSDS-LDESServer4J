package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class BucketisedMemberWriter implements ItemWriter<List<BucketisedMember>> {
	private static final String SQL = """
			INSERT INTO page_members (bucket_id, member_id)
			VALUES (?, ?)
			ON CONFLICT DO NOTHING;
			""";

	private final JdbcTemplate jdbcTemplate;

	public BucketisedMemberWriter(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void write(Chunk<? extends List<BucketisedMember>> chunk) {
		Chunk<BucketisedMember> bucketisedMembers = new Chunk<>(chunk.getItems()
				.stream()
				.flatMap(List::stream)
				.toList());

		final List<Object[]> batchArgs = bucketisedMembers.getItems().stream()
				.map(bucketisedMember -> new Object[]{bucketisedMember.bucketId(), bucketisedMember.memberId()})
				.toList();

		jdbcTemplate.batchUpdate(SQL, batchArgs);
	}
}
