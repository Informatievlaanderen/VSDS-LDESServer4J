package be.vlaanderen.informatievlaanderen.ldes.server.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.PageRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRelationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.springframework.stereotype.Service;

@Service
public class PageRelationServiceImpl implements PageRelationService {
	private final PageRepository pageRepository;
	private final PageRelationRepository pageRelationRepository;

	public PageRelationServiceImpl(PageRepository pageRepository, PageRelationRepository pageRelationRepository) {
		this.pageRepository = pageRepository;
		this.pageRelationRepository = pageRelationRepository;
	}

	@Override
	public void createGenericRelation(Page page) {
		final Page relatedPage = pageRepository.insertPage(page.createChildPage());
		final PageRelation pageRelation = PageRelation.createGenericRelation(page, relatedPage);
		pageRelationRepository.insertPageRelation(pageRelation);
	}

	@Override
	public void insertPageRelation(PageRelation pageRelation) {
		if(!pageRepository.exitsPage(pageRelation.fromPage())) {
			pageRepository.insertPage(pageRelation.fromPage());
		}
		if(!pageRepository.exitsPage(pageRelation.toPage())) {
			pageRepository.insertPage(pageRelation.toPage());
		}
		pageRelationRepository.insertPageRelation(pageRelation);
	}
}
