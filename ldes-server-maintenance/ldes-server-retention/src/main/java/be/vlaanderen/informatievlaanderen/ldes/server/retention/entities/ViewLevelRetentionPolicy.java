package be.vlaanderen.informatievlaanderen.ldes.server.retention.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

import java.util.Objects;

public record ViewLevelRetentionPolicy(ViewName viewName, RetentionPolicy retentionPolicy) implements LeveledRetentionPolicy {

	@Override
	public String getName() {
		return viewName().asString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ViewLevelRetentionPolicy that)) return false;
		return Objects.equals(viewName, that.viewName);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(viewName);
	}
}
