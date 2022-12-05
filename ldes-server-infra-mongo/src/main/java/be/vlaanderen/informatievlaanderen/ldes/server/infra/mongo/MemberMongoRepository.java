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
	public Member saveLdesMember(Member member) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(member.getLdesMemberId()));
		Update update = new Update();
		StringWriter outputStream = new StringWriter();
		RDFDataMgr.write(outputStream, member.getModel(), Lang.NQUADS);
		String ldesMemberString = outputStream.toString();
		update.set("model",ldesMemberString);
		mongoTemplate.upsert(query,update, LdesMemberEntity.class);
		return member;
	}

	@Override
	public boolean memberExists(String memberId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(memberId));
		return mongoTemplate.exists(query, LdesMemberEntity.class,"ldesmember");
	}

	@Override
	public Stream<Member> getLdesMembersByIds(List<String> ids) {
		return StreamSupport.stream(repository.findAllById(ids).spliterator(), false)
				.map(LdesMemberEntity::toLdesMember);
	}

	@Override
	public void deleteMember(String memberId) {
		repository.deleteById(memberId);
	}

	@Override
	public synchronized void addMemberReference(String ldesMemberId, String fragmentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(ldesMemberId));
		Update update = new Update();
		update.push("treeNodeReferences",fragmentId);
		mongoTemplate.upsert(query,update, LdesMemberEntity.class);
	}
}
