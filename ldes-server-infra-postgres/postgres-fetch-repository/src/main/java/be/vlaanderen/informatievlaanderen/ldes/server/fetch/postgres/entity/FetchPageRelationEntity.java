package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "page_relations")
public class FetchPageRelationEntity {
	@EmbeddedId
	private RelationId relationId;

	@ManyToOne
	@MapsId("fromPageId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "from_page_id", nullable = false, columnDefinition = "BIGINT")
	private FetchPageEntity fromPage;

	@ManyToOne
	@MapsId("toPageId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "to_page_id", nullable = false, columnDefinition = "BIGINT")
	private FetchPageEntity toPage;

	@Column(name = "relation_type", nullable = false, columnDefinition = "VARCHAR(255)")
	private String treeRelationType;

	@Column(name = "value", columnDefinition = "VARCHAR(255)")
	private String treeValue;

	@Column(name = "value_type", columnDefinition = "VARCHAR(255)")
	private String treeValueType;

	@Column(name = "path", columnDefinition = "VARCHAR(255)")
	private String treePath;

	protected FetchPageRelationEntity() {
	}

	public RelationId getRelationId() {
		return relationId;
	}

	public FetchPageEntity getToPage() {
		return toPage;
	}

	public String getTreeRelationType() {
		return treeRelationType;
	}

	public String getTreeValue() {
		return treeValue;
	}

	public String getTreeValueType() {
		return treeValueType;
	}

	public String getTreePath() {
		return treePath;
	}
}
