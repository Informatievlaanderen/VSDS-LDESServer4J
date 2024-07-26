package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.projections.BucketProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.BucketEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BucketPostgresRepositoryTest {
	private final ViewName VIEW_NAME = new ViewName("collection", "name");
	private final BucketDescriptor BUCKET_DESCRIPTOR = BucketDescriptor.fromString("key=value&k=v");
	@Mock
	private BucketEntityRepository bucketEntityRepository;
	@Mock
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@InjectMocks
	private BucketPostgresRepository bucketPostgresRepository;

	@Test
	void test_BucketRetrieval() {
		final Bucket expectedBucket = new Bucket(BUCKET_DESCRIPTOR, VIEW_NAME);
		when(bucketEntityRepository.findBucketEntityByBucketDescriptor(VIEW_NAME.asString(), BUCKET_DESCRIPTOR.asDecodedString()))
				.thenReturn(Optional.of(new BucketProjectionImpl(BUCKET_DESCRIPTOR.asDecodedString(), VIEW_NAME.asString())));

		final Optional<Bucket> retrievedBucket = bucketPostgresRepository.retrieveBucket(VIEW_NAME, BUCKET_DESCRIPTOR);

		assertThat(retrievedBucket).contains(expectedBucket);
	}

	@Test
	void test_EmptyRetrieval() {
		final Optional<Bucket> retrievedBucket = bucketPostgresRepository.retrieveBucket(VIEW_NAME, BUCKET_DESCRIPTOR);

		assertThat(retrievedBucket).isEmpty();
	}

	@Test
	void test_Insertion() {
		final Bucket bucketToSave = new Bucket(BucketDescriptor.of(new BucketDescriptorPair("key", "value"), new BucketDescriptorPair("k", "v")), VIEW_NAME);
		final Bucket expectedSavedBucket = new Bucket(101L, BucketDescriptor.of(new BucketDescriptorPair("key", "value"), new BucketDescriptorPair("k", "v")), VIEW_NAME);
		final Map<String, String> expectedSqlParams = Map.of(
				"bucket", BUCKET_DESCRIPTOR.asDecodedString(),
				"viewName", VIEW_NAME.asString()
		);
		when(namedParameterJdbcTemplate.update(anyString(), any(), any()))
				.thenAnswer(invocationOnMock -> {
					invocationOnMock.getArgument(2, KeyHolder.class).getKeyList().add(Map.of("bucket_id", 101L));
					return 1;
				});

		final Bucket result = bucketPostgresRepository.insertBucket(bucketToSave);


		verify(namedParameterJdbcTemplate).update(
				anyString(),
				assertArg(actualParams -> assertThat(actualParams)
						.asInstanceOf(InstanceOfAssertFactories.type(MapSqlParameterSource.class))
						.extracting(MapSqlParameterSource::getValues, InstanceOfAssertFactories.map(String.class, String.class))
						.containsExactlyInAnyOrderEntriesOf(expectedSqlParams)),
				any());
		assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expectedSavedBucket);
	}

	private static class BucketProjectionImpl implements BucketProjection {
		private final String bucketDescriptor, viewName;

		public BucketProjectionImpl(String bucketDescriptor, String viewName) {
			this.bucketDescriptor = bucketDescriptor;
			this.viewName = viewName;
		}

		@Override
		public Long getBucketId() {
			return 0L;
		}

		@Override
		public String getBucketDescriptor() {
			return bucketDescriptor;
		}

		@Override
		public String getViewName() {
			return viewName;
		}
	}
}