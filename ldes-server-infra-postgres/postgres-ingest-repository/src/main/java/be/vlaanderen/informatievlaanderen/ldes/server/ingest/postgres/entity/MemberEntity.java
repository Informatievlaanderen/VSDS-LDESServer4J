package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.DatabaseColumnModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.service.MemberEntityListener;
import jakarta.persistence.*;
import org.apache.jena.rdf.model.Model;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@EntityListeners(MemberEntityListener.class)
@Table(name = "members", indexes = {
        @Index(columnList = "id, timestamp")
})
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private long id;
    @Column(name = "old_id", nullable = false)
    private String oldId;
    @Column(name = "subject", nullable = false)
    private String subject;
    @ManyToOne(cascade=CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "collection_id", nullable = false)
    private EventStreamEntity collection;
    @Column(name = "version_of", nullable = false)
    private String versionOf;
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    @Column(name = "sequence_nr", nullable = false)
    private Long sequenceNr;
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;
    @Column(name = "is_in_event_source", nullable = false)
    private boolean isInEventSource;
    @Convert(converter = DatabaseColumnModelConverter.class)
    @Column(name = "member_model", nullable = false, columnDefinition = "bytea")
    private Model model;

    @SuppressWarnings("java:S107")
    public MemberEntity(String subject, EventStreamEntity collection, String versionOf, LocalDateTime timestamp, Long sequenceNr, String transactionId, boolean isInEventSource, Model model) {
        this.subject = subject;
        this.collection = collection;
        this.versionOf = versionOf;
        this.timestamp = timestamp;
        this.sequenceNr = sequenceNr;
        this.transactionId = transactionId;
        this.isInEventSource = isInEventSource;
        this.model = model;
    }
    protected MemberEntity() {}

    public long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public EventStreamEntity getCollection() {
        return collection;
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

    public boolean isInEventSource() {
        return isInEventSource;
    }

    public Model getModel() {
        return model;
    }

    public void setSequenceNr(long sequenceNr) {
        this.sequenceNr = sequenceNr;
    }
}
