package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.FragmentEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("java:S100")
public interface FragmentEntityRepository extends JpaRepository<FragmentEntity, String> {

	Optional<FragmentEntity> findLdesFragmentEntityByRootAndViewName(Boolean root, String viewName);

	List<FragmentEntity> findAllByImmutableAndViewName(Boolean immutable, String viewName);

	Optional<FragmentEntity> findByImmutableAndParentId(boolean immutable, String parentId);
	Optional<FragmentEntity> findAllByImmutableAndParentId(boolean immutable, String parentId);

	Stream<FragmentEntity> findAllByViewName(String viewName);

	List<FragmentEntity> removeByViewName(String viewName);

	Long deleteAllByCollectionName(String collectionName);

	List<FragmentEntity> findAllByRelations_TreeNode(String fragmentId);

	@Modifying
	@Transactional
	@Query(value = "WITH RECURSIVE children AS (" +
	               "SELECT id " +
	               "FROM fragmentation_fragment " +
	               "WHERE id = :parentId " +
	               "UNION ALL " +
	               "SELECT f.id " +
	               "FROM fragmentation_fragment f " +
	               "INNER JOIN children c ON f.parent_id = c.id) " +
	               "UPDATE fragmentation_fragment " +
	               "SET immutable = true " +
	               "WHERE id IN (SELECT id FROM children)", nativeQuery = true)
	int closeChildren(@Param("parentId") String parentId);

	@Transactional
	@Modifying
	@Query("UPDATE FragmentEntity f SET f.nrOfMembersAdded = f.nrOfMembersAdded + :memberCount WHERE f.id = :id")
	void incrementNrOfMembersAdded(@Param("id") String id, @Param("memberCount") Integer memberCount);

	Stream<FragmentEntity> findByDeleteTimeNotNull();
}
