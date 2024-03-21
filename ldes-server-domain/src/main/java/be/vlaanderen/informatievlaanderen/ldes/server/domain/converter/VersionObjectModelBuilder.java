package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral;

public class VersionObjectModelBuilder {
    private String memberId;
    private String versionOfPath;
    private String versionOf;
    private String timestampPath;
    private LocalDateTime timestamp;
    private Model model;

    private VersionObjectModelBuilder() {
    }

    public static VersionObjectModelBuilder create() {
        return new VersionObjectModelBuilder();
    }

    public VersionObjectModelBuilder withMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    public VersionObjectModelBuilder withVersionOfProperties(String versionOfPath, String versionOf) {
        this.versionOfPath = versionOfPath;
        this.versionOf = versionOf;
        return this;
    }


    public VersionObjectModelBuilder withTimestampProperties(String timestampPath, LocalDateTime timestamp) {
        this.timestampPath = timestampPath;
        this.timestamp = timestamp;
        return this;
    }

    public VersionObjectModelBuilder withModel(Model model) {
        this.model = model;
        return this;
    }

    public Model buildVersionObjectModel() {
        final Model versionObjectModel = ModelFactory.createDefaultModel();
        final Resource subject = createSubject(memberId);

        Map<Boolean, List<Statement>> partitionedStatements = model.listStatements().toList().stream()
                .collect(Collectors.partitioningBy(statement -> statement.getSubject().equals(ResourceFactory.createProperty(versionOf))));

        partitionedStatements.get(true)
                .forEach(statement -> versionObjectModel.add(subject, statement.getPredicate(), statement.getObject()));
        versionObjectModel.add(partitionedStatements.get(false));

        versionObjectModel.add(subject, ResourceFactory.createProperty(versionOfPath), ResourceFactory.createProperty(versionOf));
        versionObjectModel.add(subject, ResourceFactory.createProperty(timestampPath), createTimestampLiteral());

        return versionObjectModel;
    }

    private Resource createSubject(String memberId) {
        if(memberId.startsWith("http")) {
            return ResourceFactory.createProperty(memberId);
        }
        return ResourceFactory.createProperty(memberId.substring(memberId.indexOf("/") + 1));
    }

    private Literal createTimestampLiteral() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        final ZonedDateTime zoneTimestamp = timestamp.atZone(TimeZone.getDefault().toZoneId());
        calendar.setTimeInMillis(zoneTimestamp.toInstant().toEpochMilli());
        final XSDDateTime xsdTimestamp = new XSDDateTime(calendar);
        return createTypedLiteral(xsdTimestamp);
    }
}
