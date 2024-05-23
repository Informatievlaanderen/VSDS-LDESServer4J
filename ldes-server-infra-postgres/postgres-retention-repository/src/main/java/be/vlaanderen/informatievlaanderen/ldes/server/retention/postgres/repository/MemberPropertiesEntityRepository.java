package be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.entity.MemberPropertiesEntity;
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

	List<MemberPropertiesEntity> findAllByCollectionName(String collectionName);

	List<MemberPropertiesEntity> findAllByCollectionNameAndTimestampBefore(String collectionName, LocalDateTime time);

	void deleteAllByCollectionName(String collectionName);

}
