package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import java.util.*;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentEntityRepository;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

public class LdesFragmentMongoRepository implements LdesFragmentRepository {

	private final LdesFragmentEntityRepository repository;

	private final Tracer tracer;

	public LdesFragmentMongoRepository(LdesFragmentEntityRepository repository, Tracer tracer) {
		this.repository = repository;
		this.tracer = tracer;
	}

	@Override
	public LdesFragment saveFragment(LdesFragment ldesFragment) {
		repository.save(LdesFragmentEntity.fromLdesFragment(ldesFragment));
		return ldesFragment;
	}

	@Override
	public Optional<LdesFragment> retrieveFragment(LdesFragmentRequest ldesFragmentRequest) {
		Span span = tracer.nextSpan().name("Mongo FragmentEntity Retrieval").start();
		return repository
				.findLdesFragmentEntityByFragmentInfoCollectionNameAndFragmentInfoViewNameAndFragmentInfo_FragmentPairs(
						ldesFragmentRequest.collectionName(), ldesFragmentRequest.viewName(),
						ldesFragmentRequest.fragmentPairs())
				.map(ldesFragmentEntity -> {
					span.end();
					return ldesFragmentEntity.toLdesFragment();
				});
	}

	@Override
	public Optional<LdesFragment> retrieveOpenFragment(String collectionName, String viewName,
			List<FragmentPair> fragmentPairList) {
		return repository
				.findAllByFragmentInfoImmutableAndFragmentInfoCollectionNameAndFragmentInfoViewName(false,
						collectionName, viewName)
				.stream()
				.map(LdesFragmentEntity::toLdesFragment)
				.min(Comparator.comparing(LdesFragment::getFragmentId));
	}

	@Override
	public Optional<LdesFragment> retrieveChildFragment(String collectionName, String viewName,
			List<FragmentPair> fragmentPairList) {
		return repository
				.findAllByFragmentInfoImmutableAndFragmentInfoCollectionNameAndFragmentInfoViewName(false,
						collectionName, viewName)
				.stream()
				.filter(ldesFragmentEntity -> Collections
						.indexOfSubList(ldesFragmentEntity.getFragmentInfo().getFragmentPairs(), fragmentPairList) != -1
						&& !fragmentPairList.equals(ldesFragmentEntity.getFragmentInfo().getFragmentPairs()))
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
