package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PagePostgresRepository implements PageRepository {
	private final PageEntityRepository pageEntityRepository;

	public PagePostgresRepository(PageEntityRepository pageEntityRepository) {
		this.pageEntityRepository = pageEntityRepository;
	}

	@Override
	public void setChildrenImmutableByBucketId(long bucketId) {
		pageEntityRepository.setAllChildrenImmutableByBucketId(bucketId);
	}
}
