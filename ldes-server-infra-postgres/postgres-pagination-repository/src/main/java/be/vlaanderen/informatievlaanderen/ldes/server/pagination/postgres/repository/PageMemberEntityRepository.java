package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.projection.TreeMemberProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageMemberId;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PageMemberEntityRepository extends JpaRepository<PageMemberEntity, PageMemberId> {
	@Query("SELECT m.subject AS subject, m.model AS model, m.versionOf AS versionOf, m.timestamp AS timestamp FROM PageMemberEntity p JOIN p.member m WHERE p.page.id = :pageId")
	List<TreeMemberProjection> findAllMembersByPageId(long pageId);

	@Modifying
	@Query("UPDATE PageMemberEntity m SET m.page = (SELECT p FROM PageEntity p WHERE p.id = :newPageId) WHERE m.page.id IN (:pageIds)")
	void setPageMembersToNewPage(long newPageId, List<Long> pageIds);

	@Query(value = """
			 select v.name, count(*)
			 from page_members
			          JOIN buckets b on b.bucket_id = page_members.bucket_id
			          JOIN views v on v.view_id = b.view_id
			          JOIN collections c on c.collection_id = v.collection_id
			 WHERE c.name = :collectionName
			 group by v.name
			""", nativeQuery = true)
	List<Tuple> getBucketisedMemberCounts(String collectionName);

	@Query(value = """
			  select v.name, count(*)
			  from page_members
			  JOIN buckets b on b.bucket_id = page_members.bucket_id
			  JOIN views v on v.view_id = b.view_id
			  JOIN collections c on c.collection_id = v.collection_id
			  WHERE page_id IS NOT NULL AND c.name = :collectionName
			  group by v.name
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

	void deleteAllByBucket_View_EventStream_NameAndBucket_View_NameAndMember_IdIn(String collectionName, String viewName, List<Long> memberIds);
}