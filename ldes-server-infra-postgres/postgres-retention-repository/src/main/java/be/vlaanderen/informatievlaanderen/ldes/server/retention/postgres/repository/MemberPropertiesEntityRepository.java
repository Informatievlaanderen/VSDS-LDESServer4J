package be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.entity.MemberPropertiesEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.projection.RetentionMemberProjection;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Primary
public interface MemberPropertiesEntityRepository extends JpaRepository<MemberPropertiesEntity, String> {
	@Query("SELECT m FROM MemberPropertiesEntity m JOIN m.views v WHERE m.versionOf = :versionOf AND v.view = :view")
	Stream<MemberPropertiesEntity> findMembersWithVersionAndView(@Param("versionOf") String versionOf, @Param("view") String view);

	@Query("SELECT m FROM MemberPropertiesEntity m JOIN m.views v WHERE v.view = :view")
	Stream<MemberPropertiesEntity> findMembersWithView(@Param("view") String view);

	void deleteAllByCollectionName(String collectionName);

	@Query("SELECT m.id AS, m.versionOf, m.timestamp, CASE WHEN COUNT(p.id) > 0 THEN true ELSE false END, m.collection.name FROM MemberEntity m JOIN PageMemberEntity p ON m = p.member WHERE m.collection.name = :collectionName")
	List<RetentionMemberProjection> findAllByCollectionName(String collectionName);

	@Query("SELECT m.id AS, m.versionOf, m.timestamp, CASE WHEN COUNT(p.id) > 0 THEN true ELSE false END, m.collection.name FROM MemberEntity m JOIN PageMemberEntity p ON m = p.member WHERE m.collection.name = :collectionName AND CAST(m.timestamp AS timestamp) < CAST(:timestamp as timestamp)")
	Stream<RetentionMemberProjection> findAllByCollectionNameAndTimestampBefore(String collectionName, LocalDateTime timestamp);
}
