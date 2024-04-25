package be.vlaanderen.informatievlaanderen.ldes.server.fetch.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.CompactionCandidateProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface AllocationEntityRepository extends JpaRepository<MemberAllocationEntity, String> {

	@Query("SELECT DISTINCT m.memberId FROM MemberAllocationEntity m WHERE m.fragmentId IN :fragmentIds")
	List<String> findDistinctMemberIdsByFragmentIds(@Param("fragmentIds") Set<String> fragmentIds);

	@Query(value = "SELECT fragmentId as fragmentId, COUNT(*) AS size " +
	               "FROM MemberAllocationEntity " +
	               "WHERE collectionName = :collectionName AND viewName = :viewName " +
	               "GROUP BY fragmentId " +
	               "HAVING COUNT(*) < :capacityPerPage")
	Stream<CompactionCandidateProjection> findCompactionCandidates(@Param("collectionName") String collectionName,
	                                                               @Param("viewName") String viewName,
	                                                               @Param("capacityPerPage") Integer capacityPerPage);

	List<MemberAllocationEntity> findAllByFragmentId(String fragmentId);

	void deleteByMemberIdAndCollectionNameAndViewName(String memberId, String collectionName, String viewName);

	void deleteAllByCollectionNameAndViewName(String collectionName, String viewName);

	void deleteAllByCollectionName(String collectionName);

	void deleteAllByFragmentId(String fragmentId);

	void deleteAllByFragmentIdIn(Set<String> fragmentIds);

}
