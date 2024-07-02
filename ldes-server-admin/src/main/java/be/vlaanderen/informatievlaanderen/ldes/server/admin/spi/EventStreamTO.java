package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class EventStreamTO {

	private final String collection;
	private final String timestampPath;
	private final String versionOfPath;
	private final boolean versionCreationEnabled;
	private final boolean closed;
	private final List<ViewSpecification> views;
	private final Model shacl;
	private final List<Model> eventSourceRetentionPolicies;
	private final DcatDataset dcatDataset;

	@SuppressWarnings("java:S107")
	public EventStreamTO(String collection, String timestampPath, String versionOfPath, boolean versionCreationEnabled, boolean closed,
                         List<ViewSpecification> views, Model shacl, List<Model> eventSourceRetentionPolicies, DcatDataset dcatDataset) {
		this.collection = collection;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
        this.versionCreationEnabled = versionCreationEnabled;
		this.closed = closed;
        this.views = views != null ? views : new ArrayList<>();
		this.shacl = shacl;
        this.eventSourceRetentionPolicies = eventSourceRetentionPolicies;
        this.dcatDataset = dcatDataset != null ? dcatDataset : new DcatDataset(collection);
	}

	public EventStreamTO(String collection, String timestampPath, String versionOfPath, boolean versionCreationEnabled,
                         List<ViewSpecification> views, Model shacl, List<Model> eventSourceRetentionPolicies) {
		this(collection, timestampPath, versionOfPath, versionCreationEnabled, false, views, shacl, eventSourceRetentionPolicies, null);
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

	public boolean isClosed() {
		return closed;
	}

	public List<ViewSpecification> getViews() {
		return views;
	}

	public Model getShacl() {
		return shacl;
	}

	public DcatDataset getDcatDataset() {
		return dcatDataset;
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
		EventStreamTO that = (EventStreamTO) o;
		return Objects.equals(collection, that.collection) && Objects.equals(timestampPath, that.timestampPath)
				&& Objects.equals(versionOfPath, that.versionOfPath)
				&& shacl.isIsomorphicWith(that.shacl)
				&& versionCreationEnabled == that.versionCreationEnabled
				&& new HashSet<>(views).containsAll(that.views)
				&& new HashSet<>(that.views).containsAll(views)
				&& Objects.equals(dcatDataset, that.dcatDataset);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collection, timestampPath, versionOfPath, versionCreationEnabled, views, shacl, dcatDataset);
	}

	public EventStream extractEventStreamProperties() {
		return new EventStream(collection, timestampPath, versionOfPath, versionCreationEnabled);
	}
}
