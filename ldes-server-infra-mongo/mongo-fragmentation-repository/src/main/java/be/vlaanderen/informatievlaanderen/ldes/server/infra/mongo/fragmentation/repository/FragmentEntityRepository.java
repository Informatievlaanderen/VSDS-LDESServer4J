package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.FragmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("java:S100")
public interface FragmentEntityRepository extends MongoRepository<FragmentEntity, String> {

	Optional<FragmentEntity> findLdesFragmentEntityByRootAndViewName(Boolean root, String viewName);

	List<FragmentEntity> findAllByImmutableAndViewName(Boolean immutable, String viewName);

	Optional<FragmentEntity> findAllByImmutableAndParentId(boolean immutable, String parentId);

	Stream<FragmentEntity> findAllByViewName(String viewName);

	List<FragmentEntity> removeByViewName(String viewName);

	Long deleteAllByCollectionName(String collectionName);

	List<FragmentEntity> findAllByRelations_TreeNode(LdesFragmentIdentifier fragmentId);
}
