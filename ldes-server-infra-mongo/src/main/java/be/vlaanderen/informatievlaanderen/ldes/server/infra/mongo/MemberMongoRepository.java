package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class MemberMongoRepository implements MemberRepository {

	private final LdesMemberEntityRepository repository;

	@Autowired
	MongoTemplate mongoTemplate;

	public MemberMongoRepository(final LdesMemberEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public Member saveLdesMember(Member member) {
		repository.save(LdesMemberEntity.fromLdesMember(member));
		return member;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean memberExists(String memberId) {
		return repository.existsById(memberId);
	}

	@Override
	@Transactional(readOnly = true)
	public Stream<Member> getLdesMembersByIds(List<String> ids) {
		return StreamSupport.stream(repository.findAllById(ids).spliterator(), false)
				.map(LdesMemberEntity::toLdesMember);
	}

	@Override
	@Transactional
	public boolean deleteMember(String memberId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(memberId));
		query.addCriteria(Criteria.where("treeNodesReferences").elemMatch(new Criteria().exists(false)));
		return mongoTemplate.remove(query, LdesMemberEntity.class, "ldesmember").getDeletedCount()==1;
	}

	@Override
	public void addMemberReference(String memberId, String fragmentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(memberId));
		Update update = new Update();
		update.push("treeNodesReferences", fragmentId);
		mongoTemplate.upsert(query, update, LdesMemberEntity.class, "ldesmember");
	}

	@Override
	@Transactional
	public synchronized void removeMemberReference(String memberId, String treeNodeId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(memberId));
		Update update = new Update();
		update.pull("treeNodesReferences", treeNodeId);

		mongoTemplate.updateFirst(query, update, LdesFragmentEntity.class, "ldesmember");
	}
}
