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

	@Query(value = "select * from unprocessed_views", nativeQuery = true)
	List<Tuple> getUnprocessedViews();

	List<MemberEntity> findAllByCollectionNameAndSubjectIn(String collectionName, List<String> subjects);

	void deleteAllByCollectionNameAndSubjectIn(String collectionName, List<String> subjects);

}
