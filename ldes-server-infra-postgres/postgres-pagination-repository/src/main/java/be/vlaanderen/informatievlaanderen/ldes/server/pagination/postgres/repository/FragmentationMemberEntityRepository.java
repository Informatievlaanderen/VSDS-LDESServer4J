package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FragmentationMemberEntityRepository extends JpaRepository<MemberEntity, Long> {
    @Modifying
    @Query("""
            UPDATE be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.MemberEntity member 
                        SET member.isFragmented = :isFragmented 
                                    WHERE member.id IN :memberIds
            """)
    void updateIsFragmented(@Param("isFragmented") boolean isFragmented, @Param("memberIds") List<Long> memberIds);

}
