package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentEntityRepository;

public class LdesFragmentMongoRepository implements LdesFragmentRepository {

	private final LdesFragmentEntityRepository repository;

	public LdesFragmentMongoRepository(LdesFragmentEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public LdesFragment saveFragment(LdesFragment ldesFragment) {
		repository.save(LdesFragmentEntity.fromLdesFragment(ldesFragment));
		return ldesFragment;
	}

	@Override
	public Optional<LdesFragment> retrieveFragment(LdesFragmentRequest ldesFragmentRequest) {
		return repository
				.findLdesFragmentEntityByFragmentInfoCollectionNameAndFragmentInfo_FragmentPairs(
						ldesFragmentRequest.collectionName(), ldesFragmentRequest.fragmentPairs())
				.map(LdesFragmentEntity::toLdesFragment);
	}

	@Override
	public Optional<LdesFragment> retrieveOpenFragment(String collectionName) {
		return repository.findAllByFragmentInfoImmutableAndFragmentInfo_CollectionName(false, collectionName)
				.stream()
				.map(LdesFragmentEntity::toLdesFragment)
				.min(Comparator.comparing(LdesFragment::getFragmentId));
	}

	@Override
	public Optional<LdesFragment> retrieveInitialFragment(String collectionName) {
		return repository.findAll().stream()
				.filter(ldesFragmentEntity -> ldesFragmentEntity.getFragmentInfo().getCollectionName()
						.equals(collectionName))
				.map(LdesFragmentEntity::toLdesFragment)
				.min(Comparator.comparing(LdesFragment::getFragmentId));
	}

	@Override
	public List<LdesFragment> retrieveAllFragments() {
		return repository.findAll()
				.stream()
				.map(LdesFragmentEntity::toLdesFragment)
				.toList();
	}
}
