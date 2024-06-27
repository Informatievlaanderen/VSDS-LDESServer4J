package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "page_relations")
public class RelationEntity {
	@EmbeddedId
	private RelationId relationId;

	@ManyToOne
	@MapsId("fromPageId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "from_page_id", nullable = false, columnDefinition = "BIGINT")
	private PageEntity fromPage;

	@ManyToOne
	@MapsId("toPageId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "to_page_id", nullable = false, columnDefinition = "BIGINT")
	private PageEntity toPage;

	@Column(name = "relation_type", nullable = false, columnDefinition = "VARCHAR(255)")
	private String treeRelationType;

	@Column(name = "value", columnDefinition = "VARCHAR(255)")
	private String treeValue;

	@Column(name = "value_type", columnDefinition = "VARCHAR(255)")
	private String treeValueType;

	@Column(name = "path", columnDefinition = "VARCHAR(255)")
	private String treePath;


	public RelationEntity() {
	}

	public RelationEntity(PageEntity fromPage, PageEntity toPage, String treeRelationType, String treeValue, String treeValueType, String treePath) {
		this.fromPage = fromPage;
		this.toPage = toPage;
		this.treeRelationType = treeRelationType;
		this.treeValue = treeValue;
		this.treeValueType = treeValueType;
		this.treePath = treePath;
	}
}
