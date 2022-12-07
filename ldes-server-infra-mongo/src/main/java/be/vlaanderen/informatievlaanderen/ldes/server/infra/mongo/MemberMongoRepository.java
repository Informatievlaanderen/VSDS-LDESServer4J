package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Component
public class MemberMongoRepository implements MemberRepository {

	private final LdesMemberEntityRepository repository;
	private final MongoTemplate mongoTemplate;

	public MemberMongoRepository(final LdesMemberEntityRepository repository, MongoTemplate mongoTemplate) {
		this.repository = repository;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	@Transactional
	public boolean saveLdesMember(Member member) {
		try {
			mongoTemplate.insert(LdesMemberEntity.fromLdesMember(member));
			return true;
		} catch (DuplicateKeyException e) {
			return false;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Stream<Member> getLdesMembersByFragment(LdesFragment ldesFragment) {
		Query query = new Query();
		query.addCriteria(Criteria.where("treeNodesReferences").in(ldesFragment.getFragmentId()));

		return mongoTemplate.find(query, LdesMemberEntity.class, "ldesmember")
				.stream()
				.map(LdesMemberEntity::toLdesMember);
	}

	@Override
	@Transactional
	public boolean deleteMember(String memberId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(memberId));
		query.addCriteria(Criteria.where("treeNodesReferences").size(0));
		return mongoTemplate.remove(query, LdesMemberEntity.class, "ldesmember").getDeletedCount() == 1;
	}

	@Override
	@Transactional
	public boolean addMemberReference(String memberId, String fragmentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(memberId));
		Update update = new Update();
		update.push("treeNodesReferences", fragmentId);
		return mongoTemplate.updateFirst(query, update, LdesMemberEntity.class, "ldesmember")
				.getModifiedCount() == 1;
	}

	@Override
	@Transactional
	public synchronized boolean removeMemberReference(String memberId, String treeNodeId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(memberId));
		Update update = new Update();
		update.pull("treeNodesReferences", treeNodeId);

		return mongoTemplate.updateFirst(query, update, LdesFragmentEntity.class, "ldesmember")
				.getModifiedCount() == 1;
	}

	@Override
	@Transactional
	public boolean removeMemberReferencesForFragment(LdesFragment ldesFragment) {
		Query query = new Query();
		query.addCriteria(Criteria.where("treeNodesReferences").in(ldesFragment.getFragmentId()));
		Update update = new Update();
		update.pull("treeNodesReferences", ldesFragment.getFragmentId());

		return mongoTemplate.updateFirst(query, update, LdesFragmentEntity.class, "ldesmember")
				.getModifiedCount() == 1;
	}

	@Override
	@Transactional
	public long removeMembersWithNoReferences() {
		Query query = new Query();
		query.addCriteria(Criteria.where("treeNodesReferences").size(0));

		return mongoTemplate.remove(query, LdesFragmentEntity.class, "ldesmember")
				.getDeletedCount();
	}


}
