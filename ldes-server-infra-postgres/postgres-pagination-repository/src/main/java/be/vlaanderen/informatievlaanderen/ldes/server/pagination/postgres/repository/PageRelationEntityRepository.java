package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageRelationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.RelationId;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeRelationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PageRelationEntityRepository extends JpaRepository<PageRelationEntity, RelationId> {

	@Modifying
	@Query(value = """
			INSERT INTO page_relations (from_page_id, to_page_id, relation_type) VALUES (?, ?, ?)
			""", nativeQuery = true)
	void insertRelation(Long fromPageId, Long toPageId, String treeRelationType);

	@Modifying
	@Query(value = """
			INSERT INTO page_relations (from_page_id, to_page_id, relation_type, value, value_type, path)
			SELECT (SELECT page_id FROM pages WHERE partial_url = :fromPagePartialUrl),
			       (SELECT page_id FROM pages WHERE partial_url = :toPagePartialUrl),
			       :treeRelationType, :treeValue, :treeValueType, :treePath
			""", nativeQuery = true)
	void insertRelation(String fromPagePartialUrl, String toPagePartialUrl, String treeRelationType, String treeValue, String treeValueType, String treePath);

	List<TreeRelationProjection> findDistinctByFromPageId(long pageId);

	@Modifying
	@Query("UPDATE PageRelationEntity r SET r.toPage = ( SELECT p FROM PageEntity p WHERE p.id = :targetId ) " +
	"WHERE r.toPage.id IN :ids OR r.fromPage.id IN : ids")
	void updateToPageRelations(List<Long> ids, long targetId);
}
