package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.CompactionPageRelationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.repository.CompactionPageRelationEntityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CompactionPageRelationPostgresRepository implements CompactionPageRelationRepository {
	private final CompactionPageRelationEntityRepository pageRelationEntityRepository;

	public CompactionPageRelationPostgresRepository(CompactionPageRelationEntityRepository pageRelationEntityRepository) {
		this.pageRelationEntityRepository = pageRelationEntityRepository;
	}

	@Override
	@Transactional
	public void updateCompactionBucketRelations(List<Long> compactedPageIds, long targetId) {
		pageRelationEntityRepository.updateToPageRelations(compactedPageIds, targetId);
	}
}
