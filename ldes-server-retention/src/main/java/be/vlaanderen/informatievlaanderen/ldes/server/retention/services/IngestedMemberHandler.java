package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.LocalDateTimeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.EventStreamCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.valueobjects.EventStreamProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.LiteralImpl;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

@Component
public class IngestedMemberHandler {

	private final MemberPropertiesRepository memberPropertiesRepository;
	private final EventStreamCollection eventStreamCollection;
	private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

	public IngestedMemberHandler(MemberPropertiesRepository memberPropertiesRepository,
			EventStreamCollection eventStreamCollection) {
		this.memberPropertiesRepository = memberPropertiesRepository;
		this.eventStreamCollection = eventStreamCollection;
	}

	@EventListener
	public void handleMemberIngestedEvent(MemberIngestedEvent event) {
		var eventStreamProperties = eventStreamCollection.getEventStreamProperties(event.collectionName());
		LocalDateTime timestamp = getTimestamp(event.model(), eventStreamProperties).orElse(null);
		String versionOf = getVersionOf(event.model(), eventStreamProperties).orElse(null);
		MemberProperties member = new MemberProperties(event.id(), event.collectionName(), versionOf, timestamp);
		memberPropertiesRepository.saveMemberPropertiesWithoutViews(member);
	}

	private Optional<String> getVersionOf(Model model, EventStreamProperties eventStreamProperties) {
		return extractPropertyFromModel(model, eventStreamProperties.getVersionOfPath())
				.map(RDFNode::toString);
	}

	private Optional<LocalDateTime> getTimestamp(Model model, EventStreamProperties eventStreamProperties) {
		return extractPropertyFromModel(model, eventStreamProperties.getTimestampPath())
				.map(LiteralImpl.class::cast)
				.map(localDateTimeConverter::getLocalDateTime);
	}

	private Optional<RDFNode> extractPropertyFromModel(Model model, String propertyPath) {
		return model
				.listObjectsOfProperty(createProperty(propertyPath))
				.nextOptional();
	}

}
