package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BucketPartitioner implements Partitioner {
	static final String SQL = """
			SELECT bucket_id
			FROM page_members
			WHERE page_id IS NULL
			GROUP BY bucket_id
			ORDER BY COUNT (member_id) DESC
			""";

	private final JdbcTemplate jdbcTemplate;

	public BucketPartitioner(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional
	public Map<String, ExecutionContext> partition(int gridSize) {
		return jdbcTemplate.queryForList(SQL, Long.class).stream()
				.collect(Collectors.toMap(
						"bucket:%d"::formatted,
						bucketId -> new ExecutionContext(Map.of("bucketId", bucketId))
				));
	}
}
