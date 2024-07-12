package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageMemberId;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeNodeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PageMemberEntityRepository extends JpaRepository<PageMemberEntity, PageMemberId> {

}