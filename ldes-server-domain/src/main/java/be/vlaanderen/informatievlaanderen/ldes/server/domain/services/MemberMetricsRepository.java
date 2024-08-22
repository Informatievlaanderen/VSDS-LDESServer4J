package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

import java.util.List;

public interface MemberMetricsRepository {
	int getTotalCount(String collectionName);
	List<ViewName> getUnprocessedViews();
}
