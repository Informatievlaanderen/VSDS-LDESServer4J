package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.projection.TreeMemberProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PageMemberEntityRepository extends JpaRepository<PageMemberEntity, PageMemberId> {
	@Query("SELECT p.member FROM PageMemberEntity p WHERE p.page.id = :pageId")
	List<TreeMemberProjection> findAllMembersByPageId(long pageId);
}