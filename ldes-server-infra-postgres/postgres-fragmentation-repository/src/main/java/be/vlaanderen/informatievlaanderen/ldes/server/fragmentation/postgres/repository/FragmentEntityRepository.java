package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.FragmentEntity;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("java:S100")
@Primary
@Repository
public interface FragmentEntityRepository extends JpaRepository<FragmentEntity, String> {

    Optional<FragmentEntity> findLdesFragmentEntityByRootAndViewName(Boolean root, String viewName);

    List<FragmentEntity> findAllByImmutableAndViewName(Boolean immutable, String viewName);

    Optional<FragmentEntity> findByImmutableAndParentId(boolean immutable, String parentId);

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

    @Modifying
    @Query(value = "UPDATE fragmentation_fragment SET nr_of_members_added = nr_of_members_added  + :memberCount WHERE id = :id",
            nativeQuery = true)
    void incrementNrOfMembersAdded(@Param("id") String id, @Param("memberCount") Integer memberCount);

	Stream<FragmentEntity> findByDeleteTimeNotNull();

	@Modifying
	@Query(value = "UPDATE fragmentation_fragment SET immutable = true WHERE collection_name = :collectionName",
			nativeQuery = true)
	void markFragmentsImmutableInCollection(@Param("collectionName") String collectionName);
}
