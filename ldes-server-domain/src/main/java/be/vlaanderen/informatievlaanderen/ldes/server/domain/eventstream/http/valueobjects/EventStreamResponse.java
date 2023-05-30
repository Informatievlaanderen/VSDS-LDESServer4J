package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class EventStreamResponse {

	private final String collection;
	private final String timestampPath;
	private final String versionOfPath;
	private final String memberType;
	private final boolean defaultViewEnabled;
	private final List<ViewSpecification> views;
	private final Model shacl;
	private final Model dcatDataset;

	public EventStreamResponse(String collection, String timestampPath, String versionOfPath,
			String memberType, boolean defaultViewEnabled, List<ViewSpecification> views,
			Model shacl, Model dcatDataset) {
		this.collection = collection;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.memberType = memberType;
		this.defaultViewEnabled = defaultViewEnabled;
		this.views = views != null ? views : new ArrayList<>();
		this.shacl = shacl;
		this.dcatDataset = dcatDataset;
	}

	public EventStreamResponse(String collection, String timestampPath, String versionOfPath,
			String memberType, boolean defaultViewEnabled, List<ViewSpecification> views,
			Model shacl) {
		this(collection, timestampPath, versionOfPath, memberType, defaultViewEnabled, views, shacl, null);
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

	public String getMemberType() {
		return memberType;
	}

	public boolean isDefaultViewEnabled() {
		return defaultViewEnabled;
	}

	public List<ViewSpecification> getViews() {
		return views;
	}

	public Model getShacl() {
		return shacl;
	}

	public Model getDcatDataset() {
		return dcatDataset;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		EventStreamResponse that = (EventStreamResponse) o;
		boolean equalDataset;
		if (dcatDataset == null) {
			equalDataset = (that.dcatDataset == null);
		} else {
			equalDataset = dcatDataset.isIsomorphicWith(that.dcatDataset);
		}
		return Objects.equals(collection, that.collection) && Objects.equals(timestampPath, that.timestampPath)
				&& Objects.equals(versionOfPath, that.versionOfPath)
				&& Objects.equals(memberType, that.memberType)
				&& shacl.isIsomorphicWith(that.shacl)
				&& new HashSet<>(views).containsAll(that.views)
				&& new HashSet<>(that.views).containsAll(views)
				&& Objects.equals(views, that.views) && shacl.isIsomorphicWith(that.shacl)
				&& equalDataset;
	}

	@Override
	public int hashCode() {
		return Objects.hash(collection, timestampPath, versionOfPath, memberType, views, shacl, dcatDataset);
	}
}
