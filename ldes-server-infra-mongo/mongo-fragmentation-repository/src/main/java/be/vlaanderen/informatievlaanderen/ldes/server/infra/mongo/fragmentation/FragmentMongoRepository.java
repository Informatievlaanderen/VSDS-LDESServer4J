package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.FragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.FragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.resultchecker.ResultChecker;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
	public Fragment saveFragment(Fragment fragment) {
		repository.save(FragmentEntity.fromLdesFragment(fragment));
		return fragment;
	}

	@Override
	public Optional<Fragment> retrieveFragment(LdesFragmentIdentifier fragmentId) {
		return repository
				.findById(fragmentId.asString())
				.map(FragmentEntity::toLdesFragment);
	}

	@Override
	public Optional<Fragment> retrieveMutableFragment(String viewName,
			List<FragmentPair> fragmentPairList) {
		return repository
				.findAllByImmutableAndViewName(false,
						viewName)
				.stream()
				.map(FragmentEntity::toLdesFragment)
				.min(Comparator.comparing(Fragment::getFragmentIdString));
	}

	@Override
	public Optional<Fragment> retrieveOpenChildFragment(LdesFragmentIdentifier parentId) {
		return repository
				.findAllByImmutableAndParentId(false,
						parentId.asString())
				.stream()
				.map(FragmentEntity::toLdesFragment)
				.min(Comparator.comparing(Fragment::getFragmentIdString));
	}

	@Override
	public Optional<Fragment> retrieveRootFragment(String viewName) {
		return repository
				.findLdesFragmentEntityByRootAndViewName(true, viewName)
				.map(FragmentEntity::toLdesFragment);
	}

	@Override
	public void incrementNrOfMembersAdded(LdesFragmentIdentifier fragmentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(fragmentId.asString()));

		Update update = new Update().inc("nrOfMembersAdded", 1);
		UpdateResult result = mongoTemplate.updateFirst(query, update, FragmentEntity.class);
		ResultChecker.expect(result, 1);
	}

	@Override
	public Stream<Fragment> retrieveFragmentsOfView(String viewName) {
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

	@Override
	public List<Fragment> retrieveFragmentsByOutgoingRelation(LdesFragmentIdentifier ldesFragmentIdentifier) {
		return repository
				.findAllByRelations_TreeNode(ldesFragmentIdentifier)
				.stream()
				.map(FragmentEntity::toLdesFragment)
				.toList();
	}

	@Override
	public Stream<Fragment> getDeletionCandidates() {
		return repository
				.findByDeleteTimeNotNull()
				.map(FragmentEntity::toLdesFragment);
	}

	@Override
	public void removeRelationsPointingToFragmentAndDeleteFragment(Fragment readyForDeletionFragment) {
		removeRelationsPointingToDeletedFragment(readyForDeletionFragment);
		repository.delete(FragmentEntity.fromLdesFragment(readyForDeletionFragment));
	}

	private void removeRelationsPointingToDeletedFragment(Fragment readyForDeletionFragment) {
		List<FragmentEntity> fragments = repository
				.findAllByRelations_TreeNode(readyForDeletionFragment.getFragmentId());
		Set<FragmentEntity> updatedFragmentEntities = fragments
				.stream()
				.map(fragment -> {
					List<TreeRelation> relationsToRemove = fragment.getRelations().stream()
							.filter(treeRelation -> treeRelation.treeNode()
									.equals(readyForDeletionFragment.getFragmentId()))
							.toList();
					relationsToRemove.forEach(fragment::removeRelation);
					return fragment;
				}).collect(Collectors.toSet());
		repository.saveAll(updatedFragmentEntities);
	}

}
