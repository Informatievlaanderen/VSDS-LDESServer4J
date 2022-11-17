package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	@Test
	void when_retrieveFragmentIsCalled_ReturnsCorrectFragment() {
		LdesFragmentEntity ldesFragmentEntity_1 = new LdesFragmentEntity("http://server:8080/exampleData?key=1",
				false, VIEW_NAME, List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENT_VALUE_1)), false,
				LocalDateTime.now(), List.of(),
				List.of());
		LdesFragmentEntity ldesFragmentEntity_2 = new LdesFragmentEntity("http://server:8080/exampleData?key=2",
				false, VIEW_NAME, List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENT_VALUE_2)), false,
				LocalDateTime.now(), List.of(),
				List.of());
		LdesFragmentEntity ldesFragmentEntity_3 = new LdesFragmentEntity("http://server:8080/exampleData?key=3",
				false, VIEW_NAME, List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENT_VALUE_3)), false,
				LocalDateTime.now(), List.of(),
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

}
