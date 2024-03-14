package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.MemberUpdaterChange.MEMBER_COLLECTION_NAME;

@Document(MEMBER_COLLECTION_NAME)
public class MemberV1 {
    @Id
    private final String id;
    private final String collectionName;
    private final Long sequenceNr;
    private final String model;

    public MemberV1(String id, String collectionName, Long sequenceNr, String model) {
        this.id = id;
        this.collectionName = collectionName;
        this.sequenceNr = sequenceNr;
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public Long getSequenceNr() {
        return sequenceNr;
    }

    public String getModel() {
        return model;
    }

    public static MemberV1 from(MemberV2 memberV2) {
        return new MemberV1(
                memberV2.getId(),
                memberV2.getCollectionName(),
                memberV2.getSequenceNr(),
                memberV2.getModel()
        );
    }
}
