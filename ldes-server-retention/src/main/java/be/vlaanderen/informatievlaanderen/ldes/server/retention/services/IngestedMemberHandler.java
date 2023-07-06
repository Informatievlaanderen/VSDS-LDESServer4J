package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.LocalDateTimeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingStatementException;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.valueobjects.EventStreamProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.LiteralImpl;
import org.springframework.context.event.EventListener;

import java.time.LocalDateTime;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class IngestedMemberHandler {

	private final MemberRepository memberRepository;
	private final EventStreamCollection eventStreamCollection;
	private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

	public IngestedMemberHandler(MemberRepository memberRepository, EventStreamCollection eventStreamCollection) {
		this.memberRepository = memberRepository;
		this.eventStreamCollection = eventStreamCollection;
	}

	@EventListener
	public void handleEventMemberIngestedEvent(MemberIngestedEvent event) {
		EventStreamProperties eventStreamProperties = eventStreamCollection
				.getEventStreamProperties(event.collectionName());
		LocalDateTime timestamp = localDateTimeConverter
				.getLocalDateTime(extractPropertyFromModel(event.model(), eventStreamProperties.getTimestampPath()));
		String versionOf = extractPropertyFromModel(event.model(), eventStreamProperties.getVersionOfPath()).toString();
		Member member = new Member(event.id(), event.collectionName(), versionOf, timestamp);
		memberRepository.saveMember(member);
	}

	private LiteralImpl extractPropertyFromModel(Model model, String propertyPath) {
		return model
				.listStatements(null, createProperty(propertyPath), (RDFNode) null)
				.nextOptional()
				.map(statement -> (LiteralImpl) statement.getObject())
				.orElseThrow(() -> new MissingStatementException("property " + propertyPath));
	}

}
