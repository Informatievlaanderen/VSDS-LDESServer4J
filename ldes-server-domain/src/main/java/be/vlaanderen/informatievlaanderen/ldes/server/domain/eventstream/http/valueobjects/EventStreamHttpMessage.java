package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects;

import org.apache.jena.rdf.model.Model;

import java.util.Objects;

public class EventStreamHttpMessage {
	private final String collection;
	private final String timestampPath;
	private final String versionOfPath;
	private final Model views;
	private final Model shacl;

	public EventStreamHttpMessage(String collection, String timestampPath, String versionOfPath, Model views,
			Model shacl) {
		this.collection = collection;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.views = views;
		this.shacl = shacl;
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

	public Model getShacl() {
		return shacl;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		EventStreamHttpMessage that = (EventStreamHttpMessage) o;
		return Objects.equals(collection, that.collection) && Objects.equals(timestampPath, that.timestampPath)
				&& Objects.equals(versionOfPath, that.versionOfPath)
				&& views.isIsomorphicWith(that.views) && shacl.isIsomorphicWith(that.shacl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collection, timestampPath, versionOfPath, views, shacl);
	}
}
