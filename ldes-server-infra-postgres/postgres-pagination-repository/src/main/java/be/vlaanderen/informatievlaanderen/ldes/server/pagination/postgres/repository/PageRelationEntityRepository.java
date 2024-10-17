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

	List<TreeRelationProjection> findDistinctByFromPageId(long pageId);
}
