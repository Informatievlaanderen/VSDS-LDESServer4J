package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

public class LdesFragmentMongoRepository implements LdesFragmentRepository {

	private final LdesFragmentEntityRepository repository;

	@Autowired
	MongoTemplate mongoTemplate;

	public LdesFragmentMongoRepository(LdesFragmentEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public LdesFragment saveFragment(LdesFragment ldesFragment) {
		repository.save(LdesFragmentEntity.fromLdesFragment(ldesFragment));
		return ldesFragment;
	}

	@Override
	@Transactional
	public boolean removeRelationFromFragment(LdesFragment fragment, TreeRelation treeRelation) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(fragment.getFragmentId()));
		Update update = new Update();
		update.pull("relations", treeRelation);
		return mongoTemplate.updateFirst(query, update, LdesFragmentEntity.class, "ldesfragment")
				.getModifiedCount() == 1;
	}

	@Override
	@Transactional
	public boolean addRelationToFragment(LdesFragment fragment, TreeRelation treeRelation) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(fragment.getFragmentId()));
		Update update = new Update();
		update.addToSet("relations", treeRelation);
		return mongoTemplate.updateFirst(query, update, LdesFragmentEntity.class, "ldesfragment")
				.getModifiedCount() == 1;
	}

	@Override
	@Transactional
	public boolean closeFragmentAndAddNewRelation(LdesFragment completeFragment, TreeRelation treeRelation) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(completeFragment.getFragmentId()));
		Update update = new Update();
		update.set("immutable", true);
		update.addToSet("relations", treeRelation);
		return mongoTemplate.updateFirst(query, update, LdesFragmentEntity.class, "ldesfragment")
				.getModifiedCount() == 1;
	}

	@Override
	@Transactional
	public boolean setSoftDeleted(LdesFragment fragment) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(fragment.getFragmentId()));
		Update update = new Update();
		update.set("softDeleted", true);
		return mongoTemplate.updateFirst(query, update, LdesFragmentEntity.class, "ldesfragment")
				.getModifiedCount() == 1;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<LdesFragment> retrieveFragment(LdesFragmentRequest ldesFragmentRequest) {
		return repository
				.findLdesFragmentEntityByViewNameAndFragmentPairs(
						ldesFragmentRequest.viewName(),
						ldesFragmentRequest.fragmentPairs())
				.map(LdesFragmentEntity::toLdesFragment);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<LdesFragment> retrieveMutableFragment(String viewName,
			List<FragmentPair> fragmentPairList) {
		return repository
				.findAllByImmutableAndViewName(false,
						viewName)
				.stream()
				.map(LdesFragmentEntity::toLdesFragment)
				.min(Comparator.comparing(LdesFragment::getFragmentId));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<LdesFragment> retrieveOpenChildFragment(String viewName,
			List<FragmentPair> fragmentPairList) {
		Query query = new Query();
		query.addCriteria(Criteria.where("viewName").is(viewName).and("immutable").is(false));
		query.addCriteria(Criteria.where("fragmentPairs").ne(fragmentPairList).in(fragmentPairList));

		return ofNullable(mongoTemplate.findOne(query, LdesFragmentEntity.class, "ldesfragment"))
				.map(LdesFragmentEntity::toLdesFragment);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<LdesFragment> retrieveRootFragment(String viewName) {
		return repository
				.findLdesFragmentEntityByRootAndViewName(true, viewName)
				.map(LdesFragmentEntity::toLdesFragment);
	}

	@Override
	@Transactional(readOnly = true)
	public Stream<LdesFragment> retrieveNonDeletedImmutableFragmentsOfView(String viewName) {
		return repository
				.findAllByImmutableAndSoftDeletedAndViewName(true, false, viewName)
				.stream()
				.map(LdesFragmentEntity::toLdesFragment);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<LdesFragment> retrieveNonDeletedChildFragment(String viewName,
			List<FragmentPair> fragmentPairList) {
		return repository
				.findAllBySoftDeletedAndViewName(false,
						viewName)
				.stream()
				.filter(ldesFragmentEntity -> Collections
						.indexOfSubList(ldesFragmentEntity.getFragmentInfo().getFragmentPairs(), fragmentPairList) != -1
						&& !fragmentPairList.equals(ldesFragmentEntity.getFragmentInfo().getFragmentPairs()))
				.map(LdesFragmentEntity::toLdesFragment)
				.min(Comparator.comparing(LdesFragment::getFragmentId));
	}

	@Override
	@Transactional
	public boolean addMemberToFragment(LdesFragment ldesFragment, String memberId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(ldesFragment.getFragmentId()));
		Update update = new Update();
		update.addToSet("members", memberId);
		return mongoTemplate.updateFirst(query, update, LdesFragmentEntity.class, "ldesfragment")
				.getModifiedCount() == 1;
	}

}
