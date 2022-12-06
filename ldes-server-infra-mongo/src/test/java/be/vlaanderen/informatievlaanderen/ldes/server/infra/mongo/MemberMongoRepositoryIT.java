package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ActiveProfiles("mongo-test")
class MemberMongoRepositoryIT {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private MemberMongoRepository ldesMemberMongoRepository;

	@Autowired
	private LdesMemberEntityRepository ldesMemberEntityRepository;

	@AfterEach
	void cleanUpDatabase() {
		mongoTemplate.getDb().drop();
	}

	@DisplayName("Saving of an LdesMember")
	@Test
	void when_LdesMemberIsSavedInRepository_CreatedResourceIsReturned() {
		String member = String.format("""
				<http://one.example/subject1> <%s> <http://one.example/object1>.""", TREE_MEMBER);

		Member treeMember = new Member("some_id", RdfModelConverter.fromString(member, Lang.NQUADS));

		boolean memberSaved = ldesMemberMongoRepository.saveLdesMember(treeMember);

		assertTrue(memberSaved);
		assertTrue(ldesMemberEntityRepository.findById("some_id").isPresent());
	}

	@DisplayName("Saving of an existing LdesMember")
	@Test
	void when_LdesMemberExistsByIdIsRequested_ReturnsTrueWhenExisting() {
		String member = String.format("""
				<http://one.example/subject1> <%s> <http://one.example/object1>.""", TREE_MEMBER);
		Member treeMember = new Member("some_id", RdfModelConverter.fromString(member, Lang.NQUADS));

		ldesMemberEntityRepository.insert(new LdesMemberEntity("some_id", ""));

		boolean memberSaved = ldesMemberMongoRepository.saveLdesMember(treeMember);

		assertFalse(memberSaved);
		assertTrue(ldesMemberEntityRepository.findById("some_id").isPresent());
	}

	@Test
	@DisplayName("Adding fragment reference to LdesMember")
	void when_ReferenceIsAdded_ListOfReferencesIsUpdatedInMongoDB() {
		// setup
		ldesMemberEntityRepository.insert(new LdesMemberEntity("some_id", ""));

		// execute test
		boolean operationSucceeded = ldesMemberMongoRepository.addMemberReference("some_id", "fragment_id");

		// verification
		assertTrue(operationSucceeded);
		Optional<LdesMemberEntity> ldesMemberEntity = ldesMemberEntityRepository.findById("some_id");
		assertTrue(ldesMemberEntity.isPresent());
		assertEquals(1, ldesMemberEntity.get().getTreeNodesReferences().size());
		assertTrue(ldesMemberEntity.get().getTreeNodesReferences().contains("fragment_id"));
	}

	@Test
	@DisplayName("Removing fragment reference from LdesMember")
	void when_ReferenceIsRemoved_ListOfReferencesIsUpdatedInMongoDB() {
		// setup
		LdesMemberEntity newLdesMemberEntity = new LdesMemberEntity("some_id", "");
		newLdesMemberEntity.setTreeNodesReferences(Set.of("fragment_id"));
		ldesMemberEntityRepository.insert(newLdesMemberEntity);

		Optional<LdesMemberEntity> ldesMemberEntity = ldesMemberEntityRepository.findById("some_id");
		assertTrue(ldesMemberEntity.isPresent());
		assertEquals(1, ldesMemberEntity.get().getTreeNodesReferences().size());

		// execute test
		boolean operationSucceeded = ldesMemberMongoRepository.removeMemberReference("some_id", "fragment_id");

		// verification
		assertTrue(operationSucceeded);
		ldesMemberEntity = ldesMemberEntityRepository.findById("some_id");
		assertTrue(ldesMemberEntity.isPresent());
		assertEquals(0, ldesMemberEntity.get().getTreeNodesReferences().size());
	}

	@Test
	@DisplayName("Deleting LdesMember")
	void when_MemberIsDeleted_DatabaseIsUpdated() {
		// setup
		ldesMemberEntityRepository.insert(new LdesMemberEntity("some_id", ""));

		// execute test
		boolean memberDeleted = ldesMemberMongoRepository.deleteMember("some_id");

		// verification
		assertTrue(memberDeleted);
		Optional<LdesMemberEntity> ldesMemberEntity = ldesMemberEntityRepository.findById("some_id");
		assertFalse(ldesMemberEntity.isPresent());
	}

	@Test
	@DisplayName("Deleting LdesMember with reference")
	void when_MemberIsDeletedButHasReference_MemberIsNotDeleted() {
		// setup
		LdesMemberEntity newldesMemberEntity = new LdesMemberEntity("some_id", "");
		newldesMemberEntity.setTreeNodesReferences(Set.of("fragment_id"));
		ldesMemberEntityRepository.insert(newldesMemberEntity);

		// execute test
		boolean memberDeleted = ldesMemberMongoRepository.deleteMember("some_id");

		// verification
		assertFalse(memberDeleted);
		Optional<LdesMemberEntity> ldesMemberEntity = ldesMemberEntityRepository.findById("some_id");
		assertTrue(ldesMemberEntity.isPresent());
		assertTrue(ldesMemberEntity.get().getTreeNodesReferences().contains("fragment_id"));
	}
}