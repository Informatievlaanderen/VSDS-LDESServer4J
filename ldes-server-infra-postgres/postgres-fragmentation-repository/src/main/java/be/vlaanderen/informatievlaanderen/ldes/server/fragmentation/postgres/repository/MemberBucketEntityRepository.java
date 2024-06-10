package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MemberBucketEntityRepository extends PagingAndSortingRepository<MemberBucketEntity, String> {
	List<MemberBucketEntity> findAllByViewNameAndSequenceNr(String viewName, Long sequenceNr);

	void deleteAllByViewName(String viewName);

	void deleteAllByViewNameStartingWith(String collectionName);

	@Query(value = "select fb.* from fragmentation_bucketisation fb " +
	               "LEFT JOIN fetch_allocation fa ON " +
	               "fb.view_name = fa.view_name AND fb.member_id = fa.member_id " +
	               "WHERE fa.id IS NULL AND fb.view_name = :viewName AND fb.fragment_id = :fragmentId " +
	               "ORDER BY fb.id", nativeQuery = true)
	Page<MemberBucketEntity> findUnprocessedBuckets(String viewName, String fragmentId, Pageable page);
}
