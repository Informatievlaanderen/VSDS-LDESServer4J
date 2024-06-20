package be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class IngestedMember {

    public static final String TREE = "https://w3id.org/tree#";
    public static final Property TREE_MEMBER = createProperty(TREE, "member");

    private final String subject;
    private final String collectionName;
    private final String versionOf;
    private final LocalDateTime timestamp;
    private final boolean inEventSource;
    private final String transactionId;
    private final Model model;

    @SuppressWarnings("java:S107")
    public IngestedMember(String subject, String collectionName, String versionOf, LocalDateTime timestamp, boolean inEventSource, String transactionId, Model model) {
        this.subject = subject;
        this.collectionName = collectionName;
        this.versionOf = versionOf;
        this.timestamp = timestamp;
        this.inEventSource = inEventSource;
        this.transactionId = transactionId;
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public String getSubject() {
        return subject;
    }

    public void removeTreeMember() {
        getCurrentTreeMemberStatement().ifPresent(model::remove);
    }

    private Optional<Statement> getCurrentTreeMemberStatement() {
        return model.listStatements(null, TREE_MEMBER, (Resource) null).nextOptional();
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

    public boolean isInEventSource() {
        return inEventSource;
    }

    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IngestedMember member = (IngestedMember) o;
        return getSubject().equals(member.getSubject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubject());
    }

}
