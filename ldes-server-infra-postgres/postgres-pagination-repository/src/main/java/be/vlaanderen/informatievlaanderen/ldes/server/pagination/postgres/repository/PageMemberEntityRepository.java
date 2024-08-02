package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.projection.TreeMemberProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PageMemberEntityRepository extends JpaRepository<PageMemberEntity, PageMemberId> {
	@Query("SELECT m.subject AS subject, m.model AS model FROM PageMemberEntity p JOIN p.member m WHERE p.page.id = :pageId")
	List<TreeMemberProjection> findAllMembersByPageId(long pageId);

    @Modifying
    @Query("UPDATE PageMemberEntity m SET m.page = (SELECT p FROM PageEntity p WHERE p.id = :newPageId) WHERE m.page.id IN (:pageIds)")
    void setPageMembersToNewPage(long newPageId, List<Long> pageIds);

}