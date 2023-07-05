package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.LocalDateTimeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.Collection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberRepository;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.LiteralImpl;
import org.springframework.context.event.EventListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class IngestedMemberHandler {

    private final MemberRepository memberRepository;
    private final CollectionService collectionService;
    private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

    public IngestedMemberHandler(MemberRepository memberRepository, CollectionService collectionService) {
        this.memberRepository = memberRepository;
        this.collectionService = collectionService;
    }

    @EventListener
    public void handleEventMemberIngestedEvent(MemberIngestedEvent event) {
        Collection collection = collectionService.getCollection(event.collectionName());
        LocalDateTime timestamp = getLocalDateTime(extractPropertyFromModel(event.model(), collection.timestampPath()));
        String versionOf = extractPropertyFromModel(event.model(), collection.versionOfPath()).toString();
        Member member = new Member(event.id(), event.collectionName(), versionOf, timestamp);
        memberRepository.saveMember(member);
    }
    private LiteralImpl extractPropertyFromModel(Model model, String propertyPath) {
        return model
                .listStatements(null, createProperty(propertyPath), (RDFNode) null)
                .nextOptional()
                .map(statement -> (LiteralImpl) statement.getObject())
                .orElseThrow();
    }

    public LocalDateTime getLocalDateTime(LiteralImpl literal) {
        RDFDatatype datatype = literal.getDatatype();
        XSDDateTime parse = (XSDDateTime) datatype.parse(literal.getValue().toString());
        Calendar calendar = parse.asCalendar();
        TimeZone tz = calendar.getTimeZone();
        ZoneId zoneId = tz.toZoneId();
        return LocalDateTime.ofInstant(calendar.toInstant(), zoneId);
    }

}
