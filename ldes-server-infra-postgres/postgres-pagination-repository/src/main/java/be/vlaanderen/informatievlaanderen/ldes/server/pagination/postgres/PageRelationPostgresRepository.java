package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.PageRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRelationRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PageRelationPostgresRepository implements PageRelationRepository {
	@Override
	public void insertPageRelation(PageRelation pageRelation) {
		// todo
	}
}
