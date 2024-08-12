package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentationMetric;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentationMetricsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageMemberEntityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class FragmentationMetricsPostgresRepository implements FragmentationMetricsRepository {
	private final PageMemberEntityRepository entityRepository;

	public FragmentationMetricsPostgresRepository(PageMemberEntityRepository entityRepository) {
		this.entityRepository = entityRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FragmentationMetric> getBucketisedMemberCounts(String collectionName) {
		return entityRepository.getBucketisedMemberCounts(collectionName)
				.stream()
				.map(tuple -> new FragmentationMetric(tuple.get(0, String.class), tuple.get(1, Long.class).intValue()))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<FragmentationMetric> getPaginatedMemberCounts(String collectionName) {
		return entityRepository.getPaginatedMemberCounts(collectionName)
				.stream()
				.map(tuple -> new FragmentationMetric(tuple.get(0, String.class), tuple.get(1, Long.class).intValue()))
				.toList();
	}
}
