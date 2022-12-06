package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentEntityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ActiveProfiles("mongo-test")
class LdesFragmentMongoRepositoryIT {
	private static final String VIEW_NAME = "view";
	private static final String FRAGMENT_VALUE_1 = "2022-03-03T00:00:00.000Z";
	private static final String FRAGMENT_VALUE_2 = "2022-03-04T00:00:00.000Z";
	private static final String FRAGMENT_VALUE_3 = "2022-03-05T00:00:00.000Z";

	@Autowired
	private LdesFragmentMongoRepository ldesFragmentMongoRepository;

	@Autowired
	private LdesFragmentEntityRepository ldesFragmentEntityRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@AfterEach
	void tearDown() {
		mongoTemplate.getDb().drop();
	}

	@Test
	void when_retrieveFragmentIsCalled_ReturnsCorrectFragment() {
		LdesFragmentEntity ldesFragmentEntity_1 = new LdesFragmentEntity("http://server:8080/exampleData?key=1",
				false, VIEW_NAME, List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENT_VALUE_1)), false,
				false, LocalDateTime.now(), List.of(),
				List.of());
		LdesFragmentEntity ldesFragmentEntity_2 = new LdesFragmentEntity("http://server:8080/exampleData?key=2",
				false, VIEW_NAME, List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENT_VALUE_2)), false,
				false, LocalDateTime.now(), List.of(),
				List.of());
		LdesFragmentEntity ldesFragmentEntity_3 = new LdesFragmentEntity("http://server:8080/exampleData?key=3",
				false, VIEW_NAME, List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENT_VALUE_3)), false,
				false, LocalDateTime.now(), List.of(),
				List.of());

		ldesFragmentEntityRepository.saveAll(List.of(ldesFragmentEntity_1,
				ldesFragmentEntity_2, ldesFragmentEntity_3));
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENT_VALUE_2)));
		Optional<LdesFragment> ldesFragment = ldesFragmentMongoRepository.retrieveFragment(ldesFragmentRequest);

		assertTrue(ldesFragment.isPresent());
		assertEquals(ldesFragmentEntity_2.toLdesFragment().getFragmentId(),
				ldesFragment.get().getFragmentId());
	}

	@Test
	void when_addRelationToFragment_addsRelationship() {
		LdesFragment ldesFragment = new LdesFragment(new FragmentInfo("viewName", List.of()));
		TreeRelation relation = new TreeRelation("", "fragmentId", "", "",
				GENERIC_TREE_RELATION);

		ldesFragmentMongoRepository.saveFragment(ldesFragment);
		assertNotNull(mongoTemplate.findById(ldesFragment.getFragmentId(), LdesFragmentEntity.class));

		boolean operationSucceeded = ldesFragmentMongoRepository.addRelationToFragment(ldesFragment, relation);

		// Relation is added
		assertTrue(operationSucceeded);
		LdesFragmentEntity fragment = mongoTemplate.findById(ldesFragment.getFragmentId(), LdesFragmentEntity.class);
		assertNotNull(fragment);
		assertEquals(1, fragment.getRelations().size());

		operationSucceeded = ldesFragmentMongoRepository.addRelationToFragment(ldesFragment, relation);

		// Relation added already
		assertFalse(operationSucceeded);
	}

	@Test
	void when_removeRelationFromFragment_removesRelationship() {
		LdesFragment ldesFragment = new LdesFragment(new FragmentInfo("viewName", List.of()));
		TreeRelation relation = new TreeRelation("", "fragmentId", "", "",
				GENERIC_TREE_RELATION);

		ldesFragmentMongoRepository.saveFragment(ldesFragment);
		ldesFragmentMongoRepository.addRelationToFragment(ldesFragment, relation);

		boolean operationSucceeded = ldesFragmentMongoRepository.removeRelationFromFragment(ldesFragment, relation);

		// Relation is added
		assertTrue(operationSucceeded);
		LdesFragmentEntity fragment = mongoTemplate.findById(ldesFragment.getFragmentId(), LdesFragmentEntity.class);
		assertNotNull(fragment);
		assertEquals(0, fragment.getRelations().size());

		operationSucceeded = ldesFragmentMongoRepository.removeRelationFromFragment(ldesFragment, relation);

		// Relation added already
		assertFalse(operationSucceeded);
	}

	@Test
	void on_closeFragmentAndAddNewRelation_ExpectFragmentImmutableUpdatedAndRelationAdded() {
		LdesFragment ldesFragment = new LdesFragment(new FragmentInfo("viewName", List.of()));
		TreeRelation relation = new TreeRelation("", "fragmentId", "", "",
				GENERIC_TREE_RELATION);

		ldesFragmentMongoRepository.saveFragment(ldesFragment);

		// Pre Test Check
		LdesFragmentEntity dbFragment = mongoTemplate.findById(ldesFragment.getFragmentId(), LdesFragmentEntity.class);
		assertNotNull(dbFragment);
		assertFalse(dbFragment.isImmutable());

		boolean operationSucceeded = ldesFragmentMongoRepository.closeFragmentAndAddNewRelation(ldesFragment, relation);

		// Relation is added
		assertTrue(operationSucceeded);
		dbFragment = mongoTemplate.findById(ldesFragment.getFragmentId(), LdesFragmentEntity.class);
		assertNotNull(dbFragment);
		assertEquals(1, dbFragment.getRelations().size());
		assertTrue(dbFragment.getRelations().contains(relation));
		assertTrue(dbFragment.isImmutable());
	}

	@Test
	void on_setSoftDeleted_ExpectFragmentSoftDeletedToBeUpdated() {
		LdesFragment ldesFragment = new LdesFragment(new FragmentInfo("viewName", List.of()));

		ldesFragmentMongoRepository.saveFragment(ldesFragment);

		// Pre Test Check
		LdesFragmentEntity dbFragment = mongoTemplate.findById(ldesFragment.getFragmentId(), LdesFragmentEntity.class);
		assertNotNull(dbFragment);
		assertFalse(dbFragment.getFragmentInfo().getSoftDeleted());

		boolean operationSucceeded = ldesFragmentMongoRepository.setSoftDeleted(ldesFragment);

		// Relation is added
		assertTrue(operationSucceeded);
		dbFragment = mongoTemplate.findById(ldesFragment.getFragmentId(), LdesFragmentEntity.class);
		assertNotNull(dbFragment);
		assertTrue(dbFragment.getFragmentInfo().getSoftDeleted());
	}

	@Test
	void on_addMemberToFragment_ExpectFragmentMemberAdded() {
		LdesFragment ldesFragment = new LdesFragment(new FragmentInfo("viewName", List.of()));

		ldesFragmentMongoRepository.saveFragment(ldesFragment);

		// Pre Test Check
		LdesFragmentEntity dbFragment = mongoTemplate.findById(ldesFragment.getFragmentId(), LdesFragmentEntity.class);
		assertNotNull(dbFragment);
		assertEquals(0, dbFragment.getMembers().size());

		boolean operationSucceeded = ldesFragmentMongoRepository.addMemberToFragment(ldesFragment, "memberId");

		// Relation is added
		assertTrue(operationSucceeded);
		dbFragment = mongoTemplate.findById(ldesFragment.getFragmentId(), LdesFragmentEntity.class);
		assertNotNull(dbFragment);
		assertEquals(1, dbFragment.getMembers().size());

		operationSucceeded = ldesFragmentMongoRepository.addMemberToFragment(ldesFragment, "memberId");

		// Member added already
		assertFalse(operationSucceeded);
	}

}
