package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

import java.util.Map;

public interface DeletionPolicyCollection {
    Map<String, RetentionPolicy> getEventSourceRetentionPolicyMap();
    boolean isEmpty();
}
