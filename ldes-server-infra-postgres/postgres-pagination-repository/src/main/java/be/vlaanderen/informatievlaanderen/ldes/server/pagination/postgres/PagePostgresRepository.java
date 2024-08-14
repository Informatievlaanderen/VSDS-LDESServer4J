package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Repository
public class PagePostgresRepository implements PageRepository {
	private final PageEntityRepository pageEntityRepository;

	public PagePostgresRepository(PageEntityRepository pageEntityRepository) {
		this.pageEntityRepository = pageEntityRepository;
	}

	@Override
	@Transactional
	public void setChildrenImmutableByBucketId(long bucketId) {
		List<Long> pageIds = pageEntityRepository.findPageIdsByBucketId(bucketId);
		pageEntityRepository.setAllChildrenImmutable(pageIds);
	}

    @Override
    @Transactional
    public void markAllPagesImmutableByCollectionName(String collectionName) {
        pageEntityRepository.markAllPagesImmutableByCollectionName(collectionName);
    }

	@Override
	@Transactional
	public Stream<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage) {
		return pageEntityRepository.findCompactionCandidates(viewName.getCollectionName(), viewName.getViewName(), capacityPerPage)
				.stream().map(projection -> new CompactionCandidate(projection.getFragmentId(), projection.getSize(), projection.getToPage(),
						projection.getImmutable(), projection.getExpiration(), projection.getBucketId(), projection.getPartialUrl()));
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
