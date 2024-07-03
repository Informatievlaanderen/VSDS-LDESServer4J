package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BucketPartitioner implements Partitioner {
	private static final String SQL = """
			SELECT bucket_id
			FROM page_members
			WHERE page_id IS NULL
			GROUP BY bucket_id
			ORDER BY COUNT (member_id) DESC
			LIMIT ?
			""";

	private final JdbcTemplate jdbcTemplate;

	public BucketPartitioner(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		List<Long> bucketIds = jdbcTemplate.queryForList(SQL, Long.class, gridSize);

		final Map<String, ExecutionContext> contextMap = HashMap.newHashMap(bucketIds.size());
		for(Long bucketId : bucketIds) {
			final ExecutionContext context = new ExecutionContext(HashMap.newHashMap(1));
			context.putLong("bucketId", bucketId);
			contextMap.put("bucket: %d".formatted(bucketId), context);
		}
		return contextMap;
	}
}
