package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

@Component
public class BucketisedMemberWriter implements ItemWriter<List<BucketisedMember>> {

	private static final String OLD_SQL = "insert into fragmentation_bucketisation (view_name, fragment_id, member_id, sequence_nr) " +
	                                  "values (?, ?, ?, ?)";
	private static final String SQL = "INSERT INTO member_buckets (";

	private final DataSource dataSource;

	public BucketisedMemberWriter(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void write(Chunk<? extends List<BucketisedMember>> chunk) throws Exception {
		Chunk<BucketisedMember> buckets = new Chunk<>(chunk.getItems()
				.stream()
				.flatMap(List::stream)
				.toList());

		try(Connection connection = dataSource.getConnection();
		    PreparedStatement ps = connection.prepareStatement(OLD_SQL)) {
			for (BucketisedMember bucket : buckets) {
				// Set the variables
				ps.setString(1, bucket.viewName().asString());
				ps.setString(2, bucket.fragmentId());
				ps.setString(3, bucket.memberId());
				ps.setLong(4, 0L);
				// Add it to the batch
				ps.addBatch();
			}
			ps.executeBatch();
		}

	}
}
