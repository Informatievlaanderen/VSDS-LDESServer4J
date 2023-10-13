package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.statistics.service;

import org.apache.jena.atlas.json.JsonObject;

public interface StatisticsService {
	JsonObject getMetrics();
}
