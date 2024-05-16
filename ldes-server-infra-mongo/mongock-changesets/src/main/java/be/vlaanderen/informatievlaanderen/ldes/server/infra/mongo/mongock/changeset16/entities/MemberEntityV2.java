package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset16.entities;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset16.MemberEntityUpdater.MEMBER_COLLECTION_NAME;

@Document(MEMBER_COLLECTION_NAME)
public class MemberEntityV2 {
    @Id
    private final String id;
    private final String collectionName;
    private final String versionOf;
    private final LocalDateTime timestamp;
    private final Long sequenceNr;
    private final String transactionId;
    private final byte[] model;

    public MemberEntityV2(String id, String collectionName, String versionOf, LocalDateTime timestamp, Long sequenceNr, String transactionId, byte[] model) {
        this.id = id;
        this.collectionName = collectionName;
        this.versionOf = versionOf;
        this.timestamp = timestamp;
        this.sequenceNr = sequenceNr;
        this.transactionId = transactionId;
        this.model = model;
    }

    public static MemberEntityV2 from(MemberEntityV1 memberEntityV1) {
        final Model model = RDFParser.fromString(memberEntityV1.getModel()).lang(Lang.NQUADS).toModel();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        RDFWriter.source(model).lang(Lang.RDFPROTO).output(outputStream);
        return new MemberEntityV2(
                memberEntityV1.getId(),
                memberEntityV1.getCollectionName(),
                memberEntityV1.getVersionOf(),
                memberEntityV1.getTimestamp(),
                memberEntityV1.getSequenceNr(),
                memberEntityV1.getTransactionId(),
                outputStream.toByteArray()
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

    public byte[] getModel() {
        return model;
    }

}
