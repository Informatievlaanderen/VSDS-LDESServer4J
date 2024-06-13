package be.vlaanderen.informatievlaanderen.ldes.server.ingest.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
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

    public String getSubjectUri() {
        return subjectUri;
    }

    public Model getModel() {
        return model;
    }

    public IngestedMember mapToMember(String collectionName, LocalDateTime ingestedTimestamp, String txId) {
        final String memberId = "%s/%s%s%s".formatted(collectionName, subjectUri, DELIMITER, ingestedTimestamp);
        return new IngestedMember(
                memberId,
                collectionName,
                subjectUri,
                ingestedTimestamp,
                null,
                true,
                txId,
                model
        );
    }
}
