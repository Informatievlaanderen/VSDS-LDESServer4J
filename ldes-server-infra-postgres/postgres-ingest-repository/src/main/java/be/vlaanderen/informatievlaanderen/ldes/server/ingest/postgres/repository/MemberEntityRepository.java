package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberEntityRepository extends JpaRepository<MemberEntity, String> {

	@Query(value = """
			select count(*)
            from members m
	        INNER JOIN public.collections c on c.collection_id = m.collection_id
	        WHERE c.name = :collectionName
	        LIMIT 1
	""", nativeQuery = true)
	int countMemberEntitiesByColl(String collectionName);

	@Query(value = """
			select c.name, v.name, (ms.member_count - COALESCE(bs.bucketized,0)) + ps.unpaged as unprocessed, ms.member_count
			from member_stats ms
			inner join page_stats ps on ms.collection_id = ps.collection_id
			left join bucket_stats bs on ms.collection_id = bs.collection_id and ps.view_id = bs.view_id
			inner join collections c on ms.collection_id = c.collection_id
			inner join views v on ps.view_id = v.view_id
			where ((ms.member_count - COALESCE(bs.bucketized, 0)) + ps.unpaged) != 0
			""", nativeQuery = true)
	List<Tuple> getUnprocessedViews();

	@Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
			"FROM members " +
			"WHERE collection_id = :collectionId " +
			"AND subject IN :subjects",
			nativeQuery = true)
	boolean existsByCollectionAndSubjectIn(int collectionId, List<String> subjects);

	List<MemberEntity> findAllByOldIdIn(List<String> oldIds);

	List<MemberEntity> findAllByCollectionNameAndSubjectIn(String collectionName, List<String> subjects);

	void deleteAllByCollectionNameAndSubjectIn(String collectionName, List<String> subjects);

}
