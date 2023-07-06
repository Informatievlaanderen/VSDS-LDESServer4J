package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.FragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.FragmentEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class FragmentMongoRepository implements FragmentRepository {

	private static final Logger log = LoggerFactory.getLogger(FragmentMongoRepository.class);

	private final FragmentEntityRepository repository;
	private final MongoTemplate mongoTemplate;

	public FragmentMongoRepository(FragmentEntityRepository repository, MongoTemplate mongoTemplate) {
		this.repository = repository;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public LdesFragment saveFragment(LdesFragment ldesFragment) {
		repository.save(FragmentEntity.fromLdesFragment(ldesFragment));
		return ldesFragment;
	}

	@Override
	public Optional<LdesFragment> retrieveFragment(LdesFragmentIdentifier fragmentId) {
		return repository
				.findById(fragmentId.asString())
				.map(FragmentEntity::toLdesFragment);
	}

	@Override
	public Optional<LdesFragment> retrieveMutableFragment(String viewName,
			List<FragmentPair> fragmentPairList) {
		return repository
				.findAllByImmutableAndViewName(false,
						viewName)
				.stream()
				.map(FragmentEntity::toLdesFragment)
				.min(Comparator.comparing(LdesFragment::getFragmentIdString));
	}

	@Override
	public Optional<LdesFragment> retrieveOpenChildFragment(LdesFragmentIdentifier parentId) {
		return repository
				.findAllByImmutableAndParentId(false,
						parentId)
				.stream()
				.map(FragmentEntity::toLdesFragment)
				.min(Comparator.comparing(LdesFragment::getFragmentIdString));
	}

	@Override
	public Optional<LdesFragment> retrieveRootFragment(String viewName) {
		return repository
				.findLdesFragmentEntityByRootAndViewName(true, viewName)
				.map(FragmentEntity::toLdesFragment);
	}

	@Override
	public void incrementNumberOfMembers(LdesFragmentIdentifier fragmentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(fragmentId.asString()));

		Update update = new Update().inc("numberOfMembers", 1);
		mongoTemplate.updateFirst(query, update, FragmentEntity.class);
	}

	@Override
	public Stream<LdesFragment> retrieveFragmentsOfView(String viewName) {
		return repository
				.findAllByViewName(viewName)
				.map(FragmentEntity::toLdesFragment);
	}

	@Override
	public void removeLdesFragmentsOfView(String viewName) {
		repository.removeByViewName(viewName);
	}

	@Override
	public void deleteTreeNodesByCollection(String collectionName) {
		Long deleteCount = repository.deleteAllByCollectionName(collectionName);
		log.info("Deleted {} treeNodes", deleteCount);
	}

}
