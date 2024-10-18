package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.CompactionPageRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.mapper.CompactionCandidateMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.repository.CompactionPageEntityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CompactionPagePostgresRepository implements CompactionPageRepository {
	private final CompactionPageEntityRepository pageEntityRepository;

	public CompactionPagePostgresRepository(CompactionPageEntityRepository pageEntityRepository) {
		this.pageEntityRepository = pageEntityRepository;
	}

	@Override
	public List<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage) {
		return pageEntityRepository
				.findCompactionCandidates(viewName.getCollectionName(), viewName.getViewName(), capacityPerPage)
				.stream()
				.map(CompactionCandidateMapper::fromProjection)
				.toList();
	}

	@Override
	@Transactional
	public void deleteOutdatedFragments(LocalDateTime deleteTime) {
		pageEntityRepository.deleteByExpirationBefore(deleteTime);
	}

	@Override
	@Transactional
	public void setDeleteTime(List<Long> ids, LocalDateTime deleteTime) {
		pageEntityRepository.setDeleteTime(ids, deleteTime);
	}
}
