package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.datamodel;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.service.MemberEntityListener;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@EntityListeners(MemberEntityListener.class)
@Table(name = "members", indexes = {
        @Index(columnList = "collectionName"),
        @Index(columnList = "collectionName, sequenceNr")
})
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private long id;
    @Column(name = "subject", unique = true, nullable = false)
    private String subject;

    @ManyToOne(cascade=CascadeType.REMOVE)
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
    @Column(name = "member_model", nullable = false, columnDefinition = "bytea")
    private byte[] model;

    public MemberEntity(String subject, String collectionName, String versionOf, LocalDateTime timestamp, Long sequenceNr, String transactionId, boolean isInEventSource, byte[] model) {
        this.subject = subject;
        this.collectionName = collectionName;
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

    public byte[] getModel() {
        return model;
    }
}
