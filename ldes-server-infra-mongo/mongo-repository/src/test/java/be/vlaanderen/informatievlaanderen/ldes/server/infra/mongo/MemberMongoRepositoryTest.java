package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.MemberMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.entity.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.repository.LdesMemberEntityRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.MemberMongoRepository.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MemberMongoRepositoryTest {
	private final String MEMBER_ID = "memberId";
	private final String FRAGMENT_ID = "/collection/fragmentId";
	private final LdesMemberEntityRepository ldesMemberEntityRepository = mock(LdesMemberEntityRepository.class);
	private final MongoTemplate mongoTemplate = mock(MongoTemplate.class);
	private final MemberMongoRepository ldesMemberMongoRepository = new MemberMongoRepository(
			ldesMemberEntityRepository, mongoTemplate);

	@DisplayName("Correct saving of an LdesMember in MongoDB")
	@Test
	void when_LdesMemberIsSavedInRepository_CreatedResourceIsReturned() {
		Model model = getModel();

		Member treeMember = new Member("some_id", "collectionName",
				0L, "some_id", LocalDateTime.now(), model,
				List.of());

		ldesMemberMongoRepository.saveLdesMember(treeMember);

		verify(ldesMemberEntityRepository, times(1)).save(any(LdesMemberEntity.class));
	}

	@Test
	void when_LdesMemberExistsByIdIsRequested_ReturnsTrueWhenExisting() {
		String memberId = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165";
		when(mongoTemplate.exists(new Query().addCriteria(Criteria.where("_id").is(memberId)), LdesMemberEntity.class,
				"ldesmember")).thenReturn(Boolean.TRUE);

		assertTrue(ldesMemberMongoRepository.memberExists(memberId));
	}

	@Test
	void when_getMember_MemberIsReturned() {
		LdesMemberEntity ldesMemberEntity = new LdesMemberEntity(MEMBER_ID, "collectionName", 0L, MEMBER_ID,
				LocalDateTime.now(),
				getModelString(), List.of());
		when(ldesMemberEntityRepository.findById(MEMBER_ID)).thenReturn(Optional.of(ldesMemberEntity));

		Optional<Member> member = ldesMemberMongoRepository.getMember(MEMBER_ID);
		assertTrue(member.isPresent());
	}

	@Test
	void when_deleteMember_MemberIsDeleted() {
		ldesMemberMongoRepository.deleteMember(MEMBER_ID);

		verify(ldesMemberEntityRepository, times(1)).deleteById(MEMBER_ID);
	}

	@Test
	void when_RemoveMemberReference_MemberReferenceIsDeleted() {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(MEMBER_ID));
		Update update = new Update();
		update.pull(TREE_NODE_REFERENCES, FRAGMENT_ID);

		ldesMemberMongoRepository.removeMemberReference(MEMBER_ID, FRAGMENT_ID);

		verify(mongoTemplate, times(1)).upsert(query, update, LdesMemberEntity.class);
	}

	@Test
	void when_GetMembersByReference_ListOfMembersWithReferenceIsReturned() {
		Query query = new Query();
		query.addCriteria(Criteria.where(TREE_NODE_REFERENCES).is(FRAGMENT_ID));

		List<Member> members = ldesMemberMongoRepository
				.getMembersByReference(FRAGMENT_ID).toList();

		verify(mongoTemplate, times(1)).stream(query, LdesMemberEntity.class);

	}

	@Test
	void when_GetMemberStreamOfCollection_StreamOfMembersOfCollectionIsReturned() {
		Query query = new Query();
		query.addCriteria(Criteria.where(COLLECTION_NAME).is("collectionName"));
		query.with(Sort.by(SEQUENCE_NR).ascending());

		ldesMemberMongoRepository.getMemberStreamOfCollection("collectionName");

		verify(mongoTemplate, times(1)).stream(query, LdesMemberEntity.class);
	}

	@Test
	void when_GetMembersOfVersion_then_ListOfMembersWithSameVersionOfIsReturned() {
		Query query = new Query();
		query.addCriteria(Criteria.where(VERSION_OF).is("versionOf"));

		ldesMemberMongoRepository.getMembersOfVersion("versionOf");

		verify(mongoTemplate, times(1)).stream(query, LdesMemberEntity.class);
	}

	@Test
	void when_RemoveMemberReference_MemberReferenceIsDeleted2() {
		final ViewName viewName = new ViewName("collection", "view");
		final String regexMatchQueryParameters = "\\?.*";
		final String regex = viewName.asString() + regexMatchQueryParameters;
		final Query query = new Query(Criteria.where("_id").is(MEMBER_ID).and(TREE_NODE_REFERENCES).regex(regex));
		final Update update = new Update().pull(TREE_NODE_REFERENCES, new Document("$regex", regex));

		ldesMemberMongoRepository.removeViewReferenceOfMember(MEMBER_ID, viewName);

		verify(mongoTemplate, times(1)).updateMulti(query, update, LdesMemberEntity.class);
	}

	private Model getModel() {
		String member = getModelString();
		return RdfModelConverter.fromString(member, Lang.NQUADS);
	}

	private String getModelString() {
		return String.format("""
				<http://one.example/subject1> <%s> <http://one.example/object1>.""",
				TREE_MEMBER);
	}
}
