package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.services;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.valueobjects.EventStreamProperties;

import java.time.LocalDateTime;
import java.util.UUID;

public class MemberV2Builder {
    private MemberV1 memberV1;
    private final EventStreamProperties eventStreamProperties;
    private MemberV2Builder(EventStreamProperties eventStreamProperties) {
        this.eventStreamProperties = eventStreamProperties;
    }

    public static MemberV2Builder createWithEventStreamProperties(EventStreamProperties eventStreamProperties) {
        return new MemberV2Builder(eventStreamProperties);
    }

    public MemberV2Builder with(MemberV1 memberV1) {
        this.memberV1 = memberV1;
        return this;
    }

    public MemberV2 build() {
        final String transactionId = UUID.randomUUID().toString();
        return new MemberV2(
                memberV1.getId(),
                memberV1.getCollectionName(),
                extractVersionOf(),
                extractTimestamp(),
                memberV1.getSequenceNr(),
                UUID.randomUUID().toString(),
                memberV1.getModel()
        );
    }

    private String extractVersionOf() {
        return "";
    }

    private LocalDateTime extractTimestamp() {
        return null;
    }

    private String extractResource(String predicate) {

    }
}
