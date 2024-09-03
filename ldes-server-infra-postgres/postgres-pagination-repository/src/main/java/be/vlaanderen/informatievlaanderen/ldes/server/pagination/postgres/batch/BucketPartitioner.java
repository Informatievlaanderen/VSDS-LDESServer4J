package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@StepScope
public class BucketPartitioner implements Partitioner{
	static final String SQL = """
			SELECT pm.bucket_id
			FROM page_members pm
			JOIN buckets b on b.bucket_id = pm.bucket_id
			JOIN views v on v.view_id = b.view_id
			JOIN collections c on c.collection_id = v.collection_id
			WHERE c.name = ? AND v.name = ? AND page_id IS NULL
			GROUP BY pm.bucket_id
			""";

	private final JdbcTemplate jdbcTemplate;
	private final Map<String, Object> jobParameters;


	public BucketPartitioner(JdbcTemplate jdbcTemplate, @Value("#{jobParameters}") Map<String, Object> jobParameters) {
		this.jdbcTemplate = jdbcTemplate;
		this.jobParameters = jobParameters;
	}

	@Override
	@Transactional
	public Map<String, ExecutionContext> partition(int gridSize) {
		String collectionName = (String) jobParameters.get("collectionName");
		String viewName = (String) jobParameters.get("viewName");

		return jdbcTemplate.queryForList(SQL, Long.class, collectionName, viewName).stream()
				.collect(Collectors.toMap(
						"bucket:%d"::formatted,
						bucketId -> new ExecutionContext(Map.of("bucketId", bucketId))
				));
	}
}
