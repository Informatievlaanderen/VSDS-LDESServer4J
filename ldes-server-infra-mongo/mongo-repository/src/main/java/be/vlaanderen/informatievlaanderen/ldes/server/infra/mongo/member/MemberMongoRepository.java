package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.entity.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.repository.LdesMemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.service.LdesMemberEntityConverter;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MemberMongoRepository implements MemberRepository {

	private static final Logger log = LoggerFactory.getLogger(MemberMongoRepository.class);

	public static final String TREE_NODE_REFERENCES = "treeNodeReferences";
	public static final String COLLECTION_NAME = "collectionName";
	public static final String VERSION_OF = "versionOf";
	public static final String SEQUENCE_NR = "sequenceNr";
	private final LdesMemberEntityRepository repository;
	private final MongoTemplate mongoTemplate;
	private final LdesMemberEntityConverter converter = new LdesMemberEntityConverter();

	public MemberMongoRepository(final LdesMemberEntityRepository repository, MongoTemplate mongoTemplate) {
		this.repository = repository;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public Member saveLdesMember(Member member) {
		repository.save(converter.fromLdesMember(member));
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
		return repository.findById(id).map(converter::toLdesMember);
	}

	@Override
	public void deleteMember(String memberId) {
		repository.deleteById(memberId);
	}

	@Override
	public void deleteMembersByCollection(String collection) {
		Long deleteCount = repository.deleteAllByCollectionName(collection);
		log.info("Deleted {} members", deleteCount);
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
	public void removeViewReferences(ViewName viewName) {
		final String regexMatchQueryParameters = "\\?.*";
		final String regex = viewName.asString() + regexMatchQueryParameters;
		final Query query = new Query(Criteria.where(TREE_NODE_REFERENCES).regex(regex));
		final Update update = new Update().pull(TREE_NODE_REFERENCES, new Document("$regex", regex));
		mongoTemplate.updateMulti(query, update, LdesMemberEntity.class);
	}

	@Override
	public Stream<Member> getMemberStreamOfCollection(String collectionName) {
		Query query = new Query();
		query.addCriteria(Criteria.where(COLLECTION_NAME).is(collectionName));
		query.with(Sort.by(SEQUENCE_NR).ascending());
		return mongoTemplate.stream(query, LdesMemberEntity.class).map(converter::toLdesMember);
	}

	@Override
	public List<Member> getMembersOfVersion(String versionOf) {
		Query query = new Query();
		query.addCriteria(Criteria.where(VERSION_OF).is(versionOf));
		return mongoTemplate
				.stream(query, LdesMemberEntity.class)
				.map(converter::toLdesMember)
				.toList();
	}

	@Override
	public void removeViewReferenceOfMember(String memberId, ViewName viewName) {
		final String regexMatchQueryParameters = "\\?.*";
		final String regex = viewName.asString() + regexMatchQueryParameters;
		final Query query = new Query(Criteria.where("_id").is(memberId).and(TREE_NODE_REFERENCES).regex(regex));
		final Update update = new Update().pull(TREE_NODE_REFERENCES, new Document("$regex", regex));
		mongoTemplate.updateMulti(query, update, LdesMemberEntity.class);
	}

	@Override
	public Stream<Member> getMembersByReference(String treeNodeId) {
		Query query = new Query();
		query.addCriteria(Criteria.where(TREE_NODE_REFERENCES).is(treeNodeId));
		return mongoTemplate.stream(query, LdesMemberEntity.class).map(converter::toLdesMember);
	}
}
