package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("java:S100")
public interface LdesFragmentEntityRepository extends MongoRepository<LdesFragmentEntity, String> {
	Optional<LdesFragmentEntity> findLdesFragmentEntityByViewNameAndFragmentPairs(
			String viewName, List<FragmentPair> fragmentPairs);

	Optional<LdesFragmentEntity> findLdesFragmentEntityByRootAndViewName(
			Boolean root, String viewName);

	List<LdesFragmentEntity> findAllByImmutableAndViewName(
			Boolean immutable, String viewName);

}
