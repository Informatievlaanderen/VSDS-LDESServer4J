package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset16.entities;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset16.MemberEntityUpdater.MEMBER_COLLECTION_NAME;

@Document(MEMBER_COLLECTION_NAME)
public class MemberEntityV1 {
    @Id
    private final String id;
    private final String collectionName;
    private final String versionOf;
    private final LocalDateTime timestamp;
    private final Long sequenceNr;
    private final String transactionId;
    private final String model;

    public MemberEntityV1(String id, String collectionName, String versionOf, LocalDateTime timestamp, Long sequenceNr, String transactionId, String model) {
        this.id = id;
        this.collectionName = collectionName;
        this.versionOf = versionOf;
        this.timestamp = timestamp;
        this.sequenceNr = sequenceNr;
        this.transactionId = transactionId;
        this.model = model;
    }

    public static MemberEntityV1 from(MemberEntityV2 memberEntityV2) {
        final Model model = RDFParser
                .source(new ByteArrayInputStream(memberEntityV2.getModel()))
                .lang(Lang.RDFPROTO)
                .toModel();

        return new MemberEntityV1(
                memberEntityV2.getId(),
                memberEntityV2.getCollectionName(),
                memberEntityV2.getVersionOf(),
                memberEntityV2.getTimestamp(),
                memberEntityV2.getSequenceNr(),
                memberEntityV2.getTransactionId(),
                RDFWriter.source(model).lang(Lang.NQUADS).asString()
        );
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
