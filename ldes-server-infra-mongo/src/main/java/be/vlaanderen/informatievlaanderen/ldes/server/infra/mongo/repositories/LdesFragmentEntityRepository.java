package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("java:S100")
public interface LdesFragmentEntityRepository extends MongoRepository<LdesFragmentEntity, String> {
	Optional<LdesFragmentEntity> findLdesFragmentEntityByFragmentInfoCollectionNameAndFragmentInfo_FragmentPairs(
			String collectionName, List<FragmentPair> fragmentPairs);

	List<LdesFragmentEntity> findAllByFragmentInfoImmutableAndFragmentInfo_CollectionName(Boolean immutable,
			String collectionName);

}
