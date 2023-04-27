package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects;

import org.apache.jena.rdf.model.Model;

import java.util.Objects;

public class EventStream {
	private final String collection;
	private final String timestampPath;
	private final String versionOfPath;
	private final Model views;

	public EventStream(String collection, String timestampPath, String versionOfPath, Model views) {
		this.collection = collection;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.views = views;
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

	public Model getViews() {
		return views;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		EventStream that = (EventStream) o;
		return Objects.equals(collection, that.collection) && Objects.equals(timestampPath, that.timestampPath)
				&& Objects.equals(versionOfPath, that.versionOfPath) && views.isIsomorphicWith(that.views);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collection, timestampPath, versionOfPath, views);
	}
}
