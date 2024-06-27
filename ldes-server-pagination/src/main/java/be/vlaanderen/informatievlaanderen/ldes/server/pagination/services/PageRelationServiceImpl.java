package be.vlaanderen.informatievlaanderen.ldes.server.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.PageRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRelationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;

public class PageRelationServiceImpl implements PageRelationService {
	private final PageRepository pageRepository;
	private final PageRelationRepository pageRelationRepository;

	public PageRelationServiceImpl(PageRepository pageRepository, PageRelationRepository pageRelationRepository) {
		this.pageRepository = pageRepository;
		this.pageRelationRepository = pageRelationRepository;
	}

	@Override
	public void createGenericRelation(Page page) {
		final Page relatedPage = pageRepository.insertPage(page.createRelatedPage());
		final PageRelation pageRelation = PageRelation.createGenericRelation(page, relatedPage);
		pageRelationRepository.insertPageRelation(pageRelation);
	}
}
