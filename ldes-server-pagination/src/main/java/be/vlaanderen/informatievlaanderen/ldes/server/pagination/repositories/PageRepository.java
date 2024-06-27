package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;

public interface PageRepository {
	Page insertPage(Page relatedPage);
}
