package be.vlaanderen.informatievlaanderen.ldes.server.ingest.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public class MemberModel {
    private static final String DELIMITER = "/";
    private final String subjectUri;
    private final Model model;

    public MemberModel(String subjectUri, Model model) {
        this.subjectUri = subjectUri;
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public Member mapToMember(String collectionName, LocalDateTime ingestedTimestamp, String txId) {
        final String memberId = "%s/%s%s%s".formatted(collectionName, subjectUri, DELIMITER, ingestedTimestamp);
        return new Member(
                memberId,
                collectionName,
                subjectUri,
                ingestedTimestamp,
                null,
                txId,
                model
        );
    }
}
