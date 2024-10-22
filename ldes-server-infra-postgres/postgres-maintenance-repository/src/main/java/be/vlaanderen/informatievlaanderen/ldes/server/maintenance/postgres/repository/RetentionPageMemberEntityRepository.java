package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.entity.PageMemberId;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.entity.RetentionPageMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RetentionPageMemberEntityRepository extends JpaRepository<RetentionPageMemberEntity, PageMemberId> {

	@Modifying
	@Query("UPDATE PageMemberEntity m SET m.page = (SELECT p FROM PageEntity p WHERE p.id = :newPageId) WHERE m.page.id IN (:pageIds)")
	void setPageMembersToNewPage(long newPageId, List<Long> pageIds);

	@Modifying
	@Query(value = """
			DELETE FROM page_members
			WHERE member_id IN (
			    SELECT pm.member_id
			    FROM page_members pm
			             JOIN views v ON pm.view_id = v.view_id
			             JOIN collections c ON c.collection_id = v.collection_id
			    WHERE c.name = :collectionName AND v.name = :viewName AND pm.member_id IN (:memberIds)
			);
			""", nativeQuery = true)
	void removePageMembers(String collectionName, String viewName, List<Long> memberIds);
}