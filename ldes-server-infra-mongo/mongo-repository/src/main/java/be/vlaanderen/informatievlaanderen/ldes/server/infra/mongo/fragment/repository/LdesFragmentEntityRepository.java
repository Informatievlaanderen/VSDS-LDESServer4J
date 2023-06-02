package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.entity.LdesFragmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("java:S100")
public interface LdesFragmentEntityRepository extends MongoRepository<LdesFragmentEntity, String> {

	Optional<LdesFragmentEntity> findLdesFragmentEntityByRootAndViewName(
			Boolean root, String viewName);

	List<LdesFragmentEntity> findAllByImmutableAndViewName(
			Boolean immutable, String viewName);

	Optional<LdesFragmentEntity> findAllByImmutableAndParentId(boolean immutable, String parentId);

	Stream<LdesFragmentEntity> findAllByViewName(String viewName);

	List<LdesFragmentEntity> removeByViewName(String viewName);
}
