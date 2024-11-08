package be.vlaanderen.informatievlaanderen.ldes.server.ingest.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public class MemberModel {
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

    public IngestedMember mapToMember(String collectionName, String delimiter, LocalDateTime ingestedTimestamp, String txId) {
        final String memberSubject = "%s%s%s".formatted(subjectUri, delimiter, ingestedTimestamp);
        return new IngestedMember(
                memberSubject,
                collectionName,
                subjectUri,
                ingestedTimestamp,
                true,
                txId,
                model
        );
    }
}
