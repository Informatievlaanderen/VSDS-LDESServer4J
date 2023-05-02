package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Objects;

public class EventStreamResponse {
	private final String collection;
	private final String timestampPath;
	private final String versionOfPath;
	private final List<ViewSpecification> views;
	private final Model shacl;

	public EventStreamResponse(String collection, String timestampPath, String versionOfPath,
			List<ViewSpecification> views,
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

	public List<ViewSpecification> getViews() {
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
		EventStreamResponse that = (EventStreamResponse) o;
		return Objects.equals(collection, that.collection) && Objects.equals(timestampPath, that.timestampPath)
				&& Objects.equals(versionOfPath, that.versionOfPath)
				&& Objects.equals(views, that.views) && shacl.isIsomorphicWith(that.shacl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collection, timestampPath, versionOfPath, views, shacl);
	}
}
