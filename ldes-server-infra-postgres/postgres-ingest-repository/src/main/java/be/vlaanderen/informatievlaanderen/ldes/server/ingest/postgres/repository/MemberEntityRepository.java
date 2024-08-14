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

			WITH unprocessed AS (
			    select m.collection_id, v.view_id, count(*)
			    from members m
			             LEFT JOIN views v on v.collection_id = m.collection_id
			    WHERE not exists (select 1
			                      from page_members pm
			                               LEFT JOIN buckets b on b.bucket_id = pm.bucket_id
			                               LEFT JOIN views v2 on b.view_id = v2.view_id
			                      where pm.member_id = m.member_id
			                        and v.view_id = v2.view_id
			                        AND v2.collection_id = m.collection_id)
			    OR EXISTS(select 1
			              from page_members pm
			                       LEFT JOIN buckets b on b.bucket_id = pm.bucket_id
			                       LEFT JOIN views v2 on b.view_id = v2.view_id
			              where pm.member_id = m.member_id
			                and v.view_id = v2.view_id
			                AND v2.collection_id = m.collection_id
			              AND pm.page_id IS NULL)
			    GROUP BY m.collection_id, v.view_id
			    HAVING count(*) > 0
			)
			SELECT c.name, v.name from unprocessed u
			LEFT JOIN collections c ON c.collection_id = u.collection_id
			LEFT JOIN views v on v.view_id = u.view_id
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

}
