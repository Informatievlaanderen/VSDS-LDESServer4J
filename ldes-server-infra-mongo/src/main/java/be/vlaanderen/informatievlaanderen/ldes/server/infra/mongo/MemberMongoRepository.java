package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

@Component
public class MemberMongoRepository implements MemberRepository {

	public static final String TREE_NODE_REFERENCES = "treeNodeReferences";
	private final LdesMemberEntityRepository repository;

	@Autowired
	MongoTemplate mongoTemplate;

	public MemberMongoRepository(final LdesMemberEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public Member saveLdesMember(Member member) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(member.getLdesMemberId()));
		Update update = new Update();
		StringWriter outputStream = new StringWriter();
		RDFDataMgr.write(outputStream, member.getModel(), Lang.NQUADS);
		String ldesMemberString = outputStream.toString();
		update.set("model", ldesMemberString);
		mongoTemplate.upsert(query, update, LdesMemberEntity.class);
		return member;
	}

	@Override
	public boolean memberExists(String memberId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(memberId));
		return mongoTemplate.exists(query, LdesMemberEntity.class, "ldesmember");
	}

	@Override
	public Optional<Member> getMember(String id) {
		return repository.findById(id).map(LdesMemberEntity::toLdesMember);
	}

	@Override
	public void deleteMember(String memberId) {
		repository.deleteById(memberId);
	}

	@Override
	public synchronized void addMemberReference(String memberId, String fragmentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(memberId));
		Update update = new Update();
		update.push(TREE_NODE_REFERENCES, fragmentId);
		mongoTemplate.upsert(query, update, LdesMemberEntity.class);
	}

	@Override
	public void removeMemberReference(String memberId, String fragmentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(memberId));
		Update update = new Update();
		update.pull(TREE_NODE_REFERENCES, fragmentId);
		mongoTemplate.upsert(query, update, LdesMemberEntity.class);
	}

	@Override
	public List<Member> getMembersByReference(String treeNodeId) {
		Query query = new Query();
		query.addCriteria(Criteria.where(TREE_NODE_REFERENCES).is(treeNodeId));
		return mongoTemplate.find(query, LdesMemberEntity.class).stream().map(LdesMemberEntity::toLdesMember).toList();
	}
}
