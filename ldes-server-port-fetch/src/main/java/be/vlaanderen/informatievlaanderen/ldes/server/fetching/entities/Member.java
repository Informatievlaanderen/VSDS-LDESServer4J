package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;


import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.EventStreamProperties;
import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

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
        model.add(createResource(getMemberIdWithoutPrefix()), createProperty(eventStreamProperties.versionOfPath()), createProperty(versionOf));
        model.add(createResource(getMemberIdWithoutPrefix()), createProperty(eventStreamProperties.timestampPath()), createTypedLiteral(timestamp));
        return model;
    }
}
