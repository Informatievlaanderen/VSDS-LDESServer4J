package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.mapper.RetentionDatabaseColumnModelConverter;
import jakarta.persistence.*;
import org.apache.jena.rdf.model.Model;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "members", indexes = {
		@Index(columnList = "member_id, timestamp"),
		@Index(columnList = "subject, collection_id", unique = true)
})
public class RetentionMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", columnDefinition = "BIGINT", nullable = false)
    private long id;
    @Column(name = "subject", nullable = false)
    private String subject;
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "collection_id", nullable = false)
    private EventStreamEntity collection;
    @Column(name = "version_of", nullable = false)
    private String versionOf;
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;
    @Column(name = "is_in_event_source", nullable = false)
    private boolean isInEventSource;
    @Convert(converter = RetentionDatabaseColumnModelConverter.class)
    @Column(name = "member_model", nullable = false, columnDefinition = "bytea")
    private Model model;

	@SuppressWarnings("java:S107")
	public RetentionMemberEntity(String subject, EventStreamEntity collection, String versionOf, LocalDateTime timestamp, String transactionId, boolean isInEventSource, Model model) {
		this.subject = subject;
		this.collection = collection;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
		this.transactionId = transactionId;
		this.isInEventSource = isInEventSource;
		this.model = model;
	}

	public RetentionMemberEntity(long id) {
		this.id = id;
	}

	protected RetentionMemberEntity() {
	}

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

	public String getTransactionId() {
		return transactionId;
	}

	public boolean isInEventSource() {
		return isInEventSource;
	}

	public Model getModel() {
		return model;
	}
}
