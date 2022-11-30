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
	public void addRelationToFragment(LdesFragment fragment, TreeRelation treeRelation) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(fragment.getFragmentId()));
		Update update = new Update();
		update.addToSet("relations", treeRelation);
		mongoTemplate.updateFirst(query, update, LdesFragmentEntity.class, "ldesfragment");
	}

	@Override public void setSoftDeleted(LdesFragment fragment) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(fragment.getFragmentId()));
		Update update = new Update();
		update.push("softDeleted", true);
		mongoTemplate.updateFirst(query, update, LdesFragmentEntity.class, "ldesfragment");
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
		return repository
				.findAllByImmutableAndViewName(false,
						viewName)
				.stream()
				.filter(ldesFragmentEntity -> Collections
						.indexOfSubList(ldesFragmentEntity.getFragmentInfo().getFragmentPairs(), fragmentPairList) != -1
						&& !fragmentPairList.equals(ldesFragmentEntity.getFragmentInfo().getFragmentPairs()))
				.map(LdesFragmentEntity::toLdesFragment)
				.min(Comparator.comparing(LdesFragment::getFragmentId));
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
	public void addMemberToFragment(LdesFragment ldesFragment, String memberId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(ldesFragment.getFragmentId()));
		Update update = new Update();
		update.push("members", memberId);
		mongoTemplate.upsert(query, update, LdesFragmentEntity.class, "ldesfragment");
	}

}
