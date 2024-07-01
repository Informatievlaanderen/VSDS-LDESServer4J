package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.PageRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.RelationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.RelationId;

public class PageRelationMapper {
	private PageRelationMapper() {}

	public static RelationEntity toEntity(PageRelation relation) {
		final RelationId relationId = new RelationId(relation.fromPage().getPageId(), relation.toPage().getPageId());
		return new RelationEntity(relationId, relation.treeRelationType(), relation.treeValue(), relation.treeValueType(), relation.treePath());
	}
}
