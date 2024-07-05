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
			WITH view_names (view_id, view_name) AS (SELECT v.view_id, c.name || '/' || v.name FROM views v JOIN collections c ON v.collection_id = c.collection_id)
			SELECT (SELECT b.bucket_id FROM buckets b JOIN view_names v ON b.view_id = v.view_id WHERE b.bucket = ? AND v.view_name = ?), ?
			ON CONFLICT DO NOTHING;
			""";

	private final JdbcTemplate jdbcTemplate;

	public BucketisedMemberWriter(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void write(Chunk<? extends List<BucketisedMember>> chunk) throws Exception {
		Chunk<BucketisedMember> buckets = new Chunk<>(chunk.getItems()
				.stream()
				.flatMap(List::stream)
				.toList());

		final List<Object[]> batchArgs = buckets.getItems().stream()
				.map(bucket -> new Object[]{bucket.bucketDescriptor(), bucket.viewNameAsString(), bucket.memberId()})
				.toList();

		jdbcTemplate.batchUpdate(SQL, batchArgs);
	}
}
