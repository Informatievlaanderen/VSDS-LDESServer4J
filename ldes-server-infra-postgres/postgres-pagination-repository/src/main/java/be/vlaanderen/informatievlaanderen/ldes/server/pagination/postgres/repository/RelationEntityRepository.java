package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.RelationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.RelationId;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeRelationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RelationEntityRepository extends JpaRepository<RelationEntity, RelationId> {

	@Modifying
	@Query(value = """
			INSERT INTO page_relations (from_page_id, to_page_id, relation_type, value, value_type, path)
			SELECT (SELECT page_id FROM pages WHERE partial_url = :fromPagePartialUrl),
			       (SELECT page_id FROM pages WHERE partial_url = :toPagePartialUrl),
			       :treeRelationType, :treeValue, :treeValueType, :treePath
			""", nativeQuery = true)
	void insertRelation(String fromPagePartialUrl, String toPagePartialUrl, String treeRelationType, String treeValue, String treeValueType, String treePath);

	List<TreeRelationProjection> findAllByFromPageId(long pageId);
}
