package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.LocalDateTimeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.EventStreamCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.ViewCollection;
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
	private final ViewCollection viewCollection;
	private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

	public IngestedMemberHandler(MemberPropertiesRepository memberPropertiesRepository,
                                 EventStreamCollection eventStreamCollection,
								 ViewCollection viewCollection) {
		this.memberPropertiesRepository = memberPropertiesRepository;
		this.eventStreamCollection = eventStreamCollection;
        this.viewCollection = viewCollection;
    }

	@EventListener
	public void handleMemberIngestedEvent(MemberIngestedEvent event) {
		var eventStreamProperties = eventStreamCollection.getEventStreamProperties(event.collectionName());

		LocalDateTime timestamp = getTimestamp(event.model(), eventStreamProperties).orElse(null);
		String versionOf = getVersionOf(event.model(), eventStreamProperties).orElse(null);
		MemberProperties member = new MemberProperties(event.id(), event.collectionName(), versionOf, timestamp);
		addViewsToMember(member);

		memberPropertiesRepository.insert(member);
	}

	private void addViewsToMember(MemberProperties member) {
		viewCollection
				.getViews()
				.stream()
				.map(ViewSpecification::getName)
				.map(ViewName::asString)
				.forEach(member::addViewReference);
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
