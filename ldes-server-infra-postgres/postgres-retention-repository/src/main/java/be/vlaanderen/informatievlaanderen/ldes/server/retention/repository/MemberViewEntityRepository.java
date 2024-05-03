package be.vlaanderen.informatievlaanderen.ldes.server.retention.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entity.MemberViewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberViewEntityRepository extends JpaRepository<MemberViewsEntity, String> {
    @Modifying
    @Query("DELETE FROM MemberViewsEntity v WHERE v.view = :view AND v.member.id = :memberId")
    void deleteViewForMember(@Param("view") String view, @Param("memberId") String memberId);
}
