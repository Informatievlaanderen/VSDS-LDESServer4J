package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.MemberUpdaterChange.MEMBER_COLLECTION_NAME;

@Document(MEMBER_COLLECTION_NAME)
public class MemberEntityV2 {
    @Id
    private final String id;
    private final String collectionName;
    private final String versionOf;
    private final LocalDateTime timestamp;
    private final Long sequenceNr;
    private final String transactionId;
    private final String model;

    public MemberEntityV2(String id, String collectionName, String versionOf, LocalDateTime timestamp, Long sequenceNr, String transactionId, String model) {
        this.id = id;
        this.collectionName = collectionName;
        this.versionOf = versionOf;
        this.timestamp = timestamp;
        this.sequenceNr = sequenceNr;
        this.transactionId = transactionId;
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getVersionOf() {
        return versionOf;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Long getSequenceNr() {
        return sequenceNr;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getModel() {
        return model;
    }

}
