package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.exceptions.InvalidSkolemisationDomainException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.KafkaSourceProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.VersionCreationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.Model;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventStreamTO {
	private final String collection;
	private final String timestampPath;
	private final String versionOfPath;
	private final VersionCreationProperties versionCreationProperties;
	private final Optional<KafkaSourceProperties> kafkaSourceProperties;
	private final boolean closed;
	private final String skolemizationDomain;
	private final List<ViewSpecification> views;
	private final Model shacl;
	private final List<Model> eventSourceRetentionPolicies;
	private final DcatDataset dcatDataset;

	private EventStreamTO(Builder builder) {
		collection = builder.collection;
		timestampPath = builder.timestampPath;
		versionOfPath = builder.versionOfPath;
		versionCreationProperties = builder.versionCreationProperties;
		closed = builder.closed;
		skolemizationDomain = builder.skolemizationDomain;
		views = builder.views;
		shacl = builder.shacl;
		eventSourceRetentionPolicies = builder.eventSourceRetentionPolicies;
		dcatDataset = builder.dcatDataset != null ? builder.dcatDataset : new DcatDataset(builder.collection);
		kafkaSourceProperties = Optional.ofNullable(builder.kafkaSourceProperties);
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

	public String getVersionDelimiter() {
		return versionCreationProperties.getVersionDelimiter();
	}

	public boolean isVersionCreationEnabled() {
		return versionCreationProperties.isVersionCreationEnabled();
	}

	public Optional<KafkaSourceProperties> getKafkaSourceProperties() {
		return kafkaSourceProperties;
	}

	public boolean isClosed() {
		return closed;
	}

	public String getSkolemizationDomain() {
		return skolemizationDomain;
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
				&& Objects.equals(versionCreationProperties, that.versionCreationProperties)
				&& new HashSet<>(views).containsAll(that.views)
				&& new HashSet<>(that.views).containsAll(views)
				&& Objects.equals(dcatDataset, that.dcatDataset);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collection, timestampPath, versionOfPath, versionCreationProperties, views, shacl, dcatDataset);
	}

	public EventStream extractEventStreamProperties() {
		return new EventStream(collection, timestampPath, versionOfPath, versionCreationProperties, skolemizationDomain);
	}


	public static final class Builder {
		private static final Pattern SKOLEMISATION_DOMAIN_PATTERN = Pattern.compile("^(https?://)([\\w.-]+)(:\\d+)?(/.*)?$");
		private String collection;
		private String timestampPath;
		private String versionOfPath;
		private VersionCreationProperties versionCreationProperties = VersionCreationProperties.disabled();
		private KafkaSourceProperties kafkaSourceProperties;
		private boolean closed = false;
		private String skolemizationDomain;
		private List<ViewSpecification> views = List.of();
		private Model shacl;
		private List<Model> eventSourceRetentionPolicies = List.of();
		private DcatDataset dcatDataset;

		public Builder withEventStream(EventStream eventStream) {
			collection = eventStream.getCollection();
			timestampPath = eventStream.getTimestampPath();
			versionOfPath = eventStream.getVersionOfPath();
			versionCreationProperties = eventStream.getVersionCreationProperties();
			closed = eventStream.isClosed();
			skolemizationDomain = eventStream.getSkolemizationDomain().orElse(null);
			return this;
		}

		public Builder withCollection(String val) {
			collection = val;
			return this;
		}

		public Builder withTimestampPath(String val) {
			timestampPath = val;
			return this;
		}

		public Builder withVersionOfPath(String val) {
			versionOfPath = val;
			return this;
		}

		public Builder withKafkaSourceProperties(KafkaSourceProperties val) {
			kafkaSourceProperties = val;
			return this;
		}

		public Builder withVersionDelimiter(String val) {
			versionCreationProperties = VersionCreationProperties.ofNullableDelimiter(val);
			return this;
		}

		public Builder withClosed(boolean val) {
			closed = val;
			return this;
		}

		public Builder withSkolemizationDomain(String val) {
			skolemizationDomain = val;
			return this;
		}

		public Builder withViews(List<ViewSpecification> val) {
			views = val;
			return this;
		}

		public Builder withShacl(Model val) {
			shacl = val;
			return this;
		}

		public Builder withEventSourceRetentionPolicies(List<Model> val) {
			eventSourceRetentionPolicies = val;
			return this;
		}

		public Builder withDcatDataset(DcatDataset val) {
			dcatDataset = val;
			return this;
		}

		public EventStreamTO build() {
			validateSkolemizationDomain();
			return new EventStreamTO(this);
		}

		private void validateSkolemizationDomain() {
			if (skolemizationDomain != null) {
				Matcher matcher = SKOLEMISATION_DOMAIN_PATTERN.matcher(skolemizationDomain);
				if (!matcher.matches()) {
					throw new InvalidSkolemisationDomainException(skolemizationDomain);
				}
			}
		}
	}
}
