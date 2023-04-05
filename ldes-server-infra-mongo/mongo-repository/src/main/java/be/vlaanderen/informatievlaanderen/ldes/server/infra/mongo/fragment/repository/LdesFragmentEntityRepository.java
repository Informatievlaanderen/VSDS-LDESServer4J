package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.entity.LdesFragmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("java:S100")
public interface LdesFragmentEntityRepository extends MongoRepository<LdesFragmentEntity, String> {

	Optional<LdesFragmentEntity> findLdesFragmentEntityByRootAndViewName(
			Boolean root, String viewName);

	List<LdesFragmentEntity> findAllByImmutableAndViewName(
			Boolean immutable, String viewName);

	List<LdesFragmentEntity> findAllByImmutableAndSoftDeletedAndViewName(Boolean immutable, Boolean softDeleted,
			String viewName);

	List<LdesFragmentEntity> findAllBySoftDeletedAndViewName(Boolean softDeleted, String viewName);

	Optional<LdesFragmentEntity> findAllByImmutableAndParentId(boolean immutable, String parentId);
}
