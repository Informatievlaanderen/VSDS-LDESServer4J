package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

import java.util.List;
import java.util.Map;

public interface RetentionPolicyCollection {

	Map<String, List<RetentionPolicy>> getRetentionPolicyMap();
}
