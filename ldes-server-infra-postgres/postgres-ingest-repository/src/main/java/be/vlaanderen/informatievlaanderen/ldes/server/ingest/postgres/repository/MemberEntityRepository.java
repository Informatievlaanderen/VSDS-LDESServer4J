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
			   select col, view
			   from (select c.name as col, v.name as view, count(*), (select count(*) from members) , count(*) = (select count(*) from members) as keptUp
			         from views v
			                  JOIN collections c on v.collection_id = c.collection_id
			                  LEFT JOIN buckets b on v.view_id = b.view_id
			                  LEFT JOIN page_members pm on b.bucket_id = pm.bucket_id
			         where pm.member_id IS NOT NULL
			         GROUP BY c.name, v.name) as fragmentProcess
			   where keptUp = false
			   UNION
			   select DISTINCT c.name as col, v.name as view from views v
			       JOIN collections c on v.collection_id = c.collection_id
			       LEFT OUTER JOIN buckets b ON b.view_id = v.view_id
			       WHERE b.bucket_id IS NULL
			""", nativeQuery = true)
	List<Tuple> getUnprocessedCollections();

	@Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
			"FROM members " +
			"WHERE collection_id = :collectionId " +
			"AND subject IN :subjects",
			nativeQuery = true)
	boolean existsByCollectionAndSubjectIn(int collectionId, List<String> subjects);

	List<MemberEntity> findAllByOldIdIn(List<String> oldIds);

	List<MemberEntity> findAllByCollectionNameAndSubjectIn(String collectionName, List<String> subjects);

	void deleteAllByCollectionNameAndSubjectIn(String collectionName, List<String> subjects);

	@Query(value = """
        SELECT count(*) > 0
        FROM members m
                 LEFT JOIN collections c ON c.name = :collectionName
        WHERE NOT EXISTS (
            select * from page_members mb
                              LEFT JOIN views v ON v.name = :viewName AND v.collection_id = c.collection_id
                              LEFT JOIN buckets b ON b.view_id = v.view_id
            where mb.member_id = m.member_id and mb.bucket_id = b.bucket_id
        )
    """, nativeQuery = true)
	Boolean viewIsUnprocessed(String collectionName, String viewName);
}
