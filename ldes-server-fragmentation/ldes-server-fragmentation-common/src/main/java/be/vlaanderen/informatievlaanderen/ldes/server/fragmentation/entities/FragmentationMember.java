package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.VersionObjectModelBuilder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.EventStreamProperties;
import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public final class FragmentationMember {
	private final long memberId;
	private final String subject;
	private final String versionOf;
	private final LocalDateTime timestamp;
	private final EventStreamProperties eventStreamProperties;
	private final Model model;

	public FragmentationMember(
			long memberId,
			String subject,
			String versionOf,
			LocalDateTime timestamp,
			EventStreamProperties eventStreamProperties,
			Model model
	) {
		this.memberId = memberId;
		this.subject = subject;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
		this.eventStreamProperties = eventStreamProperties;
		this.model = model;
	}

	public long getMemberId() {
		return memberId;
	}

	public String getSubject() {
		return subject;
	}

	public String getCollectionName() {
		return eventStreamProperties.collectionName();
	}

	public Model getVersionModel() {
		final String subjectUri = subject.startsWith("http") ? subject : subject.substring(subject.indexOf("/") + 1);
		if(!eventStreamProperties.versionCreationEnabled()) {
			return model;
		}
		return VersionObjectModelBuilder.create()
				.withMemberSubject(subjectUri)
				.withVersionOfProperties(eventStreamProperties.versionOfPath(), versionOf)
				.withTimestampProperties(eventStreamProperties.timestampPath(), timestamp)
				.withModel(model)
				.buildVersionObjectModel();

	}
}