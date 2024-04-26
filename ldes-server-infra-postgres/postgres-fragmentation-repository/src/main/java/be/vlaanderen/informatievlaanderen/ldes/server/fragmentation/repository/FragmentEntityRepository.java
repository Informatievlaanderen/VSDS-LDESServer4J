package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.FragmentEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("java:S100")
public interface FragmentEntityRepository extends JpaRepository<FragmentEntity, String> {

	Optional<FragmentEntity> findLdesFragmentEntityByRootAndViewName(Boolean root, String viewName);

	List<FragmentEntity> findAllByImmutableAndViewName(Boolean immutable, String viewName);

	Optional<FragmentEntity> findAllByImmutableAndParentId(boolean immutable, String parentId);

	Stream<FragmentEntity> findAllByViewName(String viewName);

	List<FragmentEntity> removeByViewName(String viewName);

	Long deleteAllByCollectionName(String collectionName);

	List<FragmentEntity> findAllByRelations_TreeNode(String fragmentId);

	@Modifying
	@Transactional
	@Query("UPDATE FragmentEntity f SET f.immutable = true WHERE f.id = :id AND :params <= f.fragmentPairs")
	int setImmutableForMatchingPairs(String id, Map<String, String> params);

	@Transactional
	@Modifying
	@Query("UPDATE FragmentEntity f SET f.nrOfMembersAdded = f.nrOfMembersAdded + :memberCount WHERE f.id = :id")
	void incrementNrOfMembersAdded(@Param("id") String id, @Param("memberCount") Integer memberCount);

	Stream<FragmentEntity> findByDeleteTimeNotNull();
}
