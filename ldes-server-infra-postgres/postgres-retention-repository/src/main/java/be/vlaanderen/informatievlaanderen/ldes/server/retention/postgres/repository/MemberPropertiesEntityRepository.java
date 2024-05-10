package be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.entity.MemberPropertiesEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.projection.MemberPropertyVersionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public interface MemberPropertiesEntityRepository extends JpaRepository<MemberPropertiesEntity, String> {
	@Query("SELECT m FROM MemberPropertiesEntity m JOIN m.views v WHERE m.versionOf = :versionOf AND v.view = :view")
	Stream<MemberPropertiesEntity> findMembersWithVersionAndView(@Param("versionOf") String versionOf, @Param("view") String view);

	@Query("SELECT m FROM MemberPropertiesEntity m JOIN m.views v WHERE v.view = :view")
	Stream<MemberPropertiesEntity> findMembersWithView(@Param("view") String view);

	Stream<MemberPropertiesEntity> findAllByCollectionName(String collectionName);

	@Query("SELECT rmp.id as id, rmp.versionOf as versionOf, rmp.timestamp as timestamp, " +
	       "rmp.collectionName as collectionName, rmp.views as views, ROW_NUMBER() OVER(PARTITION BY rmp.versionOf ORDER BY timestamp DESC) as versionNumber " +
	       "FROM MemberPropertiesEntity rmp JOIN rmp.views rmv " +
	       "WHERE rmp.collectionName = :collectionName AND rmv.view = :view and rmp.timestamp < :timestamp")
	List<MemberPropertyVersionProjection> findExpiredMemberPropertiesBeforeTimestamp(@Param("collectionName") String collectionName,
	                                                                                 @Param("view") String view,
	                                                                                 @Param("timestamp") LocalDateTime timestamp);

	@Query("SELECT rmp.id as id, rmp.versionOf as versionOf, rmp.timestamp as timestamp, " +
	       "rmp.collectionName as collectionName, rmp.views as views, ROW_NUMBER() OVER(PARTITION BY rmp.versionOf ORDER BY timestamp DESC) as versionNumber " +
	       "FROM MemberPropertiesEntity rmp JOIN rmp.views rmv " +
	       "WHERE rmp.collectionName = :collectionName AND rmv.view = :view ")
	List<MemberPropertyVersionProjection> findExpiredMemberProperties(String collectionName,
	                                                                    String view);


	void deleteAllByCollectionName(String collectionName);

}
