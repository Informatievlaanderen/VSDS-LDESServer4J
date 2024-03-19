package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.membermapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

import static org.apache.jena.rdf.model.ResourceFactory.*;

public class MemberMapper {
    private final String versionOfPath;
    private final String timestampPath;

    public MemberMapper(String versionOfPath, String timestampPath) {
        this.versionOfPath = versionOfPath;
        this.timestampPath = timestampPath;
    }

    public Member mapToFragmentationMember(be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member ingestMember) {
        return new Member(ingestMember.getId(), enrichModel(ingestMember), ingestMember.getSequenceNr());
    }

    private Model enrichModel(be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member ingestMember) {
        final Model modelToEnrich = ModelFactory.createModelForGraph(ingestMember.getModel().getGraph());
        final Resource subject = createSubject(ingestMember.getId());

        modelToEnrich.add(subject, ResourceFactory.createProperty(versionOfPath), ResourceFactory.createProperty(ingestMember.getVersionOf()));
        modelToEnrich.add(subject, ResourceFactory.createProperty(timestampPath), createTimestampLiteral(ingestMember.getTimestamp()));

        return modelToEnrich;
    }

    private Resource createSubject(String memberId) {
        String subjectUri = memberId.startsWith("http") ? memberId : memberId.substring(memberId.indexOf("/") + 1);
        return ResourceFactory.createResource(subjectUri);
    }

    private Literal createTimestampLiteral(LocalDateTime timestamp) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        final ZonedDateTime zoneTimestamp = timestamp.atZone(TimeZone.getDefault().toZoneId());
        calendar.setTimeInMillis(zoneTimestamp.toInstant().toEpochMilli());
        final XSDDateTime xsdTimestamp = new XSDDateTime(calendar);
        return createTypedLiteral(xsdTimestamp);
    }
}
