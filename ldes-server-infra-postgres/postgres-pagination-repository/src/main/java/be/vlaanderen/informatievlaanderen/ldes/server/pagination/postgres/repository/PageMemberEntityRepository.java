package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageMemberId;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PageMemberEntityRepository extends JpaRepository<PageMemberEntity, PageMemberId> {

	@Modifying
	@Query("UPDATE PageMemberEntity m SET m.page = (SELECT p FROM PageEntity p WHERE p.id = :newPageId) WHERE m.page.id IN (:pageIds)")
	void setPageMembersToNewPage(long newPageId, List<Long> pageIds);

	@Query(value = """
			select v.name, vs.bucketized_count as count
			from view_stats vs
			JOIN views v on v.view_id = vs.view_id
			JOIN collections c on c.collection_id = v.collection_id
			WHERE c.name = :collectionName
			""", nativeQuery = true)
	List<Tuple> getBucketisedMemberCounts(String collectionName);

	@Query(value = """
        select v.name, vs.paginated_count as count
        from view_stats vs
        JOIN views v on v.view_id = vs.view_id
        JOIN collections c on c.collection_id = v.collection_id
        WHERE c.name = :collectionName
        """, nativeQuery = true)
	List<Tuple> getPaginatedMemberCounts(String collectionName);

	@Modifying
	@Query("""
			UPDATE PageMemberEntity p SET p.page = :page WHERE p.pageMemberId.bucketId = :bucketId
			AND p.pageMemberId.memberId IN :memberIds
			""")
	void updatePageForMembers(@Param("page") PageEntity page, @Param("bucketId") Long bucketId, @Param("memberIds") List<Long> memberIds);

	@Query("SELECT pm.pageMemberId.memberId FROM PageMemberEntity pm WHERE pm.pageMemberId.bucketId = :bucketId AND " +
	       "pm.page IS NULL ORDER BY pm.pageMemberId.memberId")
	List<Long> findByBucketIdAndPageIdIsNullOrderByMemberId(long bucketId);

	@Override
	@EntityGraph(attributePaths = {"page"})
	List<PageMemberEntity> findAll();

}