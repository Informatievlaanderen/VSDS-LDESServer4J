package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;


import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.EventStreamProperties;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

import static org.apache.jena.rdf.model.ResourceFactory.*;

public class Member {
    private final String id;
    private final EventStreamProperties eventStreamProperties;
    private final String versionOf;
    private final LocalDateTime timestamp;
    private final Model model;

    public Member(String id, EventStreamProperties eventStreamProperties, String versionOf, LocalDateTime timestamp, Model model) {
        this.id = id;
        this.eventStreamProperties = eventStreamProperties;
        this.versionOf = versionOf;
        this.timestamp = timestamp;
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public String getMemberIdWithoutPrefix() {
        if (id.startsWith("http")) {
            throw new IllegalStateException("id '%s' does not contain a prefix".formatted(id));
        }
        return id.substring(id.indexOf("/") + 1);
    }

    public Model getModel() {
        if (!model.contains(null, createProperty(eventStreamProperties.versionOfPath()), (RDFNode) null)) {
            model.add(createResource(getMemberIdWithoutPrefix()), createProperty(eventStreamProperties.versionOfPath()), createProperty(versionOf));
        }
        if (!model.contains(null, createProperty(eventStreamProperties.timestampPath()), (RDFNode) null)) {
            enrichModelWithTimestamp();
        }
        return model;
    }

    private void enrichModelWithTimestamp() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        ZonedDateTime zoneTimestamp = timestamp.atZone(TimeZone.getDefault().toZoneId());
        calendar.setTimeInMillis(zoneTimestamp.toInstant().toEpochMilli());
        final XSDDateTime xsdTimestamp = new XSDDateTime(calendar);
        model.add(createResource(getMemberIdWithoutPrefix()), createProperty(eventStreamProperties.timestampPath()), createTypedLiteral(xsdTimestamp));
    }
}
