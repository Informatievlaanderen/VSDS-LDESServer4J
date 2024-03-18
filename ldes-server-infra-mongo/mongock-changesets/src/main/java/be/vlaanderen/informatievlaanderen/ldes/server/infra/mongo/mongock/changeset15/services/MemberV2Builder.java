package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.LocalDateTimeConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.entities.MemberEntityV1;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.entities.MemberEntityV2;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.valueobjects.EventStreamProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.time.LocalDateTime;
import java.util.UUID;

public class MemberV2Builder {
    private MemberEntityV1 memberEntityV1;
    private final EventStreamProperties eventStreamProperties;
    private Model jenaModel;

    private MemberV2Builder(EventStreamProperties eventStreamProperties) {
        this.eventStreamProperties = eventStreamProperties;
    }

    public static MemberV2Builder createWithEventStreamProperties(EventStreamProperties eventStreamProperties) {
        return new MemberV2Builder(eventStreamProperties);
    }

    public MemberV2Builder with(MemberEntityV1 memberEntityV1) {
        this.memberEntityV1 = memberEntityV1;
        return this;
    }

    public MemberEntityV2 build() {
        final String transactionId = UUID.randomUUID().toString();
        return new MemberEntityV2(
                memberEntityV1.getId(),
                memberEntityV1.getCollectionName(),
                extractVersionOf(),
                extractTimestamp(),
                memberEntityV1.getSequenceNr(),
                transactionId,
                memberEntityV1.getModel()
        );
    }

    private Model getJenaModel() {
        if (jenaModel == null) {
            jenaModel = RDFParser.fromString(memberEntityV1.getModel()).lang(Lang.NQ).toModel();
        }
        return jenaModel;
    }

    private String extractVersionOf() {
        final String versionOfPath = eventStreamProperties.versionOfPath();
        return getJenaModel().listObjectsOfProperty(ResourceFactory.createProperty(versionOfPath))
                .nextOptional()
                .map(RDFNode::toString)
                .orElseThrow(() -> new IllegalStateException("Database model does not contain expected %s".formatted(versionOfPath)));
    }

    private LocalDateTime extractTimestamp() {
        final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();
        final String timestampPath = eventStreamProperties.timestampPath();
        return getJenaModel()
                .listObjectsOfProperty(ResourceFactory.createProperty(timestampPath))
                .nextOptional()
                .map(RDFNode::asLiteral)
                .map(localDateTimeConverter::getLocalDateTime)
                .orElseThrow(() -> new IllegalStateException("Database model does not contain expected %s".formatted(timestampPath)));
    }
}
