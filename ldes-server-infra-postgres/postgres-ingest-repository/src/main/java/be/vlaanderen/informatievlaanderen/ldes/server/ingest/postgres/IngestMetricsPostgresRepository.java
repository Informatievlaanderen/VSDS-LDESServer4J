package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.MemberMetricsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class IngestMetricsPostgresRepository implements MemberMetricsRepository {
	private final MemberEntityRepository repository;

	public IngestMetricsPostgresRepository(MemberEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public int getTotalCount(String collectionName) {
		return repository.countMemberEntitiesByColl(collectionName);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ViewName> getUnprocessedCollections() {
		return repository.getUnprocessedCollections()
				.stream()
				.map(tuple -> new ViewName(tuple.get(0, String.class), tuple.get(1, String.class)))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean viewIsUnprocessed(ViewName viewName) {
		return repository.viewIsUnprocessed(viewName.getCollectionName(), viewName.getViewName());
	}
}
