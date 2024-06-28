package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper.PageMapper;
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
	public Page insertPage(Page page) {
		final PageEntity insertedPage = pageEntityRepository.insert(page.bucket().getBucketId(), page.expiration(), page.partialUrl());
		return PageMapper.fromEntity(insertedPage);
	}

	@Override
	public boolean exitsPage(Page relatedPage) {
		return false;
	}
}
