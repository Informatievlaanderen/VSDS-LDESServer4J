package be.vlaanderen.informatievlaanderen.ldes.server.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.PageRelation;

public interface PageRelationService {
	void createGenericRelation(Page page);
	void insertPageRelation(PageRelation pageRelation);
}
