package be.vlaanderen.informatievlaanderen.ldes.server.retention.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

import java.util.Objects;

public record EventSourceRetentionPolicyProvider(String collectionName,
                                                 RetentionPolicy retentionPolicy) implements RetentionPolicyProvider {

	@Override
	public String getName() {
		return collectionName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventSourceRetentionPolicyProvider that)) return false;
		return Objects.equals(collectionName, that.collectionName);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(collectionName);
	}
}
