package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper.MemberModelConverter;
import jakarta.persistence.*;
import org.apache.jena.rdf.model.Model;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity(name = "PaginationMemberEntity")
@Table(name = "members", indexes = {
		@Index(columnList = "member_id, timestamp"),
		@Index(columnList = "subject, collection_id", unique = true)
})
public class MemberEntity {
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
	@Convert(converter = MemberModelConverter.class)
	@Column(name = "member_model", nullable = false, columnDefinition = "bytea")
	private Model model;
    @Column(name = "is_fragmented", nullable = false)
    private boolean isFragmented;

	public MemberEntity(long id) {
		this.id = id;
	}

	protected MemberEntity() {
	}

	public long getId() {
		return id;
	}

    public boolean isFragmented() {
        return isFragmented;
    }

    public void setFragmented(boolean fragmented) {
        isFragmented = fragmented;
    }
}
