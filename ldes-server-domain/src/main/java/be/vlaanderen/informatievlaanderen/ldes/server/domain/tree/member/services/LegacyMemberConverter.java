package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.LocalDateTimeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.LiteralImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

@Component
public class LegacyMemberConverter {

	private final EventStreamService eventStreamService;

	private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

	public LegacyMemberConverter(EventStreamService eventStreamService) {
		this.eventStreamService = eventStreamService;
	}

	Member toMember(String collectionName, Model memberModel) {
		EventStreamResponse eventStream = eventStreamService.retrieveEventStream(collectionName);

		String memberId = extractMemberId(memberModel, eventStream.getMemberType(), collectionName);
		String versionOf = extractVersionOf(memberModel, eventStream.getVersionOfPath());
		LocalDateTime timestamp = extractTimestamp(memberModel, eventStream.getTimestampPath());
		return new Member(memberId, collectionName, null, versionOf, timestamp, memberModel, List.of());
	}

	private LocalDateTime extractTimestamp(Model memberModel, String timestampPath) {
		LiteralImpl literalImpl = memberModel
				.listStatements(null, createProperty(timestampPath), (RDFNode) null)
				.nextOptional()
				.map(statement -> (LiteralImpl) statement.getObject())
				.orElse(null);
		if (literalImpl == null) {
			return null;
		}
		return localDateTimeConverter.getLocalDateTime(literalImpl);

	}

	private String extractVersionOf(Model memberModel, String versionOfPath) {
		return memberModel
				.listStatements(null, createProperty(versionOfPath), (RDFNode) null)
				.nextOptional()
				.map(statement -> statement.getObject().toString())
				.orElse(null);
	}

	private String extractMemberId(Model model, String memberType, String collectionName) {
		return model
				.listStatements(null, RDF_SYNTAX_TYPE, createResource(memberType))
				.nextOptional()
				.map(statement -> collectionName + "/" + statement.getSubject().toString())
				.orElseThrow(() -> new IllegalArgumentException(memberType));
	}

}
