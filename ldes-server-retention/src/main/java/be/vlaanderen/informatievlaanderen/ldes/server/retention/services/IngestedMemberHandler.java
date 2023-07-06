package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.LocalDateTimeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingStatementException;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.valueobjects.EventStreamProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.LiteralImpl;
import org.springframework.context.event.EventListener;

import java.time.LocalDateTime;
import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

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
		EventStreamProperties eventStreamProperties = eventStreamCollection
				.getEventStreamProperties(event.collectionName());
		LocalDateTime timestamp = localDateTimeConverter
				.getLocalDateTime(extractPropertyFromModel(event.model(), eventStreamProperties.getTimestampPath()));
		String versionOf = extractPropertyFromModel(event.model(), eventStreamProperties.getVersionOfPath()).toString();
		MemberProperties member = new MemberProperties(event.id(), event.collectionName(), List.of(), versionOf,
				timestamp);
		memberPropertiesRepository.save(member);
	}

	private LiteralImpl extractPropertyFromModel(Model model, String propertyPath) {
		return model
				.listStatements(null, createProperty(propertyPath), (RDFNode) null)
				.nextOptional()
				.map(statement -> (LiteralImpl) statement.getObject())
				.orElseThrow(() -> new MissingStatementException("property " + propertyPath));
	}

}
