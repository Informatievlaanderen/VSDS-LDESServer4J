package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Objects;

public class EventStream {

	private final String collection;
	private final String timestampPath;
	private final String versionOfPath;
	private final boolean versionCreationEnabled;
	private final List<Model> eventSourceRetentionPolicies;

	public EventStream(String collection, String timestampPath, String versionOfPath, boolean versionCreationEnabled,
					   List<Model> retentionPolicies) {
		this.collection = collection;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
        this.versionCreationEnabled = versionCreationEnabled;
		this.eventSourceRetentionPolicies = retentionPolicies;
    }

	public String getCollection() {
		return collection;
	}

	public String getTimestampPath() {
		return timestampPath;
	}

	public String getVersionOfPath() {
		return versionOfPath;
	}

	public boolean isVersionCreationEnabled() {
		return versionCreationEnabled;
	}

	public List<Model> getEventSourceRetentionPolicies() {
		return eventSourceRetentionPolicies;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		EventStream that = (EventStream) o;
		return Objects.equals(collection, that.collection);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collection);
	}
}
