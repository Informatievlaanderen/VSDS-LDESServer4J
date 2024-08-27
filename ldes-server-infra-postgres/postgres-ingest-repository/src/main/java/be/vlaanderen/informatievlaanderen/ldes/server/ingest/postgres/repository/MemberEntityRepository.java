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
			select c.name, v.name, count(*)
			from page_members pm
			         RIGHT JOIN members m on pm.member_id = m.member_id
			         JOIN collections c on c.collection_id = m.collection_id
			         JOIN views v on c.collection_id = v.collection_id
			where (pm.member_id IS NULL OR pm.page_id IS NULL)
			  AND (NOT EXISTS (SELECT 1 FROM page_members) OR m.member_id >  (select max(member_id)
			                     from page_members pmx
			                              JOIN public.buckets b on b.bucket_id = pmx.bucket_id
			                              JOIN public.views v2 on v2.view_id = b.view_id
			                     WHERE v2.view_id = v.view_id
			                       AND v2.collection_id = c.collection_id))
			GROUP BY c.collection_id, v.view_id
			HAVING (count(*) > 0)
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
