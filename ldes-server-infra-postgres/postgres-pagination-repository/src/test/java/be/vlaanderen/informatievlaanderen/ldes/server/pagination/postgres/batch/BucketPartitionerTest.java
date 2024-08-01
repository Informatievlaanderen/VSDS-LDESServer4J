package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch.BucketPartitioner.SQL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BucketPartitionerTest {
	private static final int GRID_SIZE = 3;
	@Mock
	private JdbcTemplate jdbcTemplate;
	@InjectMocks
	private BucketPartitioner bucketPartitioner;

	private static Stream<Arguments> provideBucketIds() {
		return Stream.of(
				Arguments.of(List.of(12L, 24L, 55L)),
				Arguments.of(List.of(12L, 24L)),
				Arguments.of(List.of())
		);
	}

	@MethodSource("provideBucketIds")
	@ParameterizedTest
	void test_Partition(List<Long> bucketIds) {
		final Map<String, ExecutionContext> expectedContexts = bucketIds.stream()
						.collect(Collectors.toMap("bucket: %d"::formatted, id -> new ExecutionContext(Map.of("bucket_id", id))));
		when(jdbcTemplate.queryForList(SQL, Long.class, GRID_SIZE)).thenReturn(bucketIds);

		final Map<String, ExecutionContext> contexts = bucketPartitioner.partition(GRID_SIZE);

		assertThat(contexts)
				.hasSameSizeAs(bucketIds)
				.containsKeys(expectedContexts.keySet().toArray(new String[0]));
	}
}