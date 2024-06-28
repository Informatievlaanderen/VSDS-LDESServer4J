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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class BucketisedMemberWriter implements ItemWriter<List<BucketisedMember>> {

	private static final String OLD_SQL = "insert into fragmentation_bucketisation (view_name, fragment_id, member_id, sequence_nr) " +
			"values (?, ?, ?, ?)";
	private static final String SQL = """
			INSERT INTO page_members (bucket_id, member_id)
			SELECT (SELECT bucket_id FROM buckets WHERE bucket = ?),
			       (SELECT member_id FROM members  WHERE subject = ?)
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

		final List<Object[]> batchArgs = new ArrayList<>();
		for (BucketisedMember bucket : buckets) {
			if (!bucket.fragmentId().isEmpty()) {
				batchArgs.add(new Object[]{bucket.fragmentId(), bucket.memberId()});
			}
		}
		jdbcTemplate.batchUpdate(SQL, batchArgs);
	}

	private void temporaryOldSaving(Chunk<BucketisedMember> buckets) throws SQLException {
		try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
		     PreparedStatement ps = connection.prepareStatement(OLD_SQL)) {
			ps.setLong(4, 0L);
			for (BucketisedMember bucket : buckets) {
				// Set the variables
				ps.setString(1, bucket.viewName().asString());
				ps.setString(2, bucket.viewNameAsString() + '?' + bucket.fragmentId());
				ps.setString(3, bucket.viewName().getCollectionName() + '/' + bucket.memberId());
				// Add it to the batch
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}
}
