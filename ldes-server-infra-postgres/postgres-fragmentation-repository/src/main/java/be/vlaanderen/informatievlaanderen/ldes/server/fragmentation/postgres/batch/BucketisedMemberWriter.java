package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
public class BucketisedMemberWriter implements ItemWriter<List<BucketisedMember>> {

	private static final String OLD_SQL = "insert into fragmentation_bucketisation (view_name, fragment_id, member_id, sequence_nr) " +
			"values (?, ?, ?, ?)";
	private static final String SQL = """
			INSERT INTO page_members (bucket_id, member_id)
			WITH view_names (view_id, view_name) AS (SELECT v.view_id, c.name || '/' || v.name FROM views v JOIN collections c ON v.collection_id = c.collection_id)
			SELECT (SELECT b.bucket_id FROM buckets b JOIN view_names v ON b.view_id = v.view_id WHERE b.bucket = ? AND v.view_name = ?),
			       (SELECT member_id FROM members WHERE subject = ?)
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

		temporaryOldSaving(buckets);

		final List<Object[]> batchArgs = buckets.getItems().stream()
				.map(bucket -> new Object[]{bucket.fragmentId(), bucket.viewNameAsString(), bucket.memberId()})
				.toList();

		jdbcTemplate.batchUpdate(SQL, batchArgs);
	}

	private void temporaryOldSaving(Chunk<BucketisedMember> buckets) throws SQLException {
		try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
		     PreparedStatement ps = connection.prepareStatement(OLD_SQL)) {
			ps.setLong(4, 0L);
			for (BucketisedMember bucket : buckets) {
				// Set the variables
				ps.setString(1, bucket.viewName().asString());
				ps.setString(2, bucket.viewNameAsString() + (bucket.fragmentId().isEmpty() ? "" : '?' + bucket.fragmentId()));
				ps.setString(3, bucket.viewName().getCollectionName() + '/' + bucket.memberId());
				// Add it to the batch
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}
}
