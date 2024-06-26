package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "page_relations")
public class RelationEntity {
	@EmbeddedId
	private RelationId relationId;

	@ManyToOne
	@MapsId("fromPage")
	private PageEntity fromPage;

	@ManyToOne
	@MapsId("toPage")
	private PageEntity toPage;

	@Column(name = "value", nullable = false, columnDefinition = "VARCHAR(255)")
	private String treeValue;

	@Column(name = "value_type", nullable = false, columnDefinition = "VARCHAR(255)")
	private String treeValueType;

	@Column(name = "relation_type", nullable = false, columnDefinition = "VARCHAR(255)")
	private String treeRelationType;

	public RelationEntity() {
	}

	public RelationEntity(PageEntity fromPage, PageEntity toPage, String treeValue, String treeValueType, String treeRelationType) {
		this.fromPage = fromPage;
		this.toPage = toPage;
		this.treeValue = treeValue;
		this.treeValueType = treeValueType;
		this.treeRelationType = treeRelationType;
	}
}
