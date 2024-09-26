package be.vlaanderen.informatievlaanderen.ldes.server.retention.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

public interface LeveledRetentionPolicy {
	String getName();
	RetentionPolicy retentionPolicy();

}
