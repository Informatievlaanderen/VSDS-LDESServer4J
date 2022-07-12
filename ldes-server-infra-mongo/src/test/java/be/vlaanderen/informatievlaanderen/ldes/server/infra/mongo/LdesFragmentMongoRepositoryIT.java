package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ActiveProfiles("mongo-test")
class LdesFragmentMongoRepositoryIT {
    private static final String VIEW_SHORTNAME = "exampleData";
    private static final String VIEW = "http://localhost:8089/exampleData";
    private static final String SHAPE = "http://localhost:8089/exampleData/shape";
    private static final String PATH = "http://www.w3.org/ns/prov#generatedAtTime";
    private static final String FRAGMENT_VALUE_1 = "2022-03-03T00:00:00.000Z";
    private static final String FRAGMENT_VALUE_2 = "2022-03-04T00:00:00.000Z";
    private static final String FRAGMENT_VALUE_3 = "2022-03-05T00:00:00.000Z";

    @Autowired
    private LdesFragmentMongoRepository ldesFragmentMongoRepository;

    @Autowired
    private LdesFragmentEntityRepository ldesFragmentEntityRepository;

    @Test
    void when_retrieveFragmentIsCalledWithUnknownValue_ReturnsClosestFragment() {
        LdesFragmentEntity ldesFragmentEntity_1 = new LdesFragmentEntity("http://server:8080/exampleData?key=1", fragmentInfo(FRAGMENT_VALUE_1), List.of(), List.of());
        LdesFragmentEntity ldesFragmentEntity_2 = new LdesFragmentEntity("http://server:8080/exampleData?key=1", fragmentInfo(FRAGMENT_VALUE_2), List.of(), List.of());
        LdesFragmentEntity ldesFragmentEntity_3 = new LdesFragmentEntity("http://server:8080/exampleData?key=1", fragmentInfo(FRAGMENT_VALUE_3), List.of(), List.of());

        ldesFragmentEntityRepository.saveAll(List.of(ldesFragmentEntity_1, ldesFragmentEntity_2, ldesFragmentEntity_3));
        Optional<LdesFragment> ldesFragment = ldesFragmentMongoRepository.retrieveFragment(VIEW_SHORTNAME, PATH, "2022-03-04T18:00:00.000Z");

        assertTrue(ldesFragment.isPresent());
        assertEquals(ldesFragmentEntity_2.toLdesFragment().getFragmentId(), ldesFragment.get().getFragmentId());
    }

    @Test
    void when_retrieveFragmentIsCalled_ReturnsCorrectFragment() {
        LdesFragmentEntity ldesFragmentEntity_1 = new LdesFragmentEntity("http://server:8080/exampleData?key=1", fragmentInfo(FRAGMENT_VALUE_1), List.of(), List.of());
        LdesFragmentEntity ldesFragmentEntity_2 = new LdesFragmentEntity("http://server:8080/exampleData?key=1", fragmentInfo(FRAGMENT_VALUE_2), List.of(), List.of());
        LdesFragmentEntity ldesFragmentEntity_3 = new LdesFragmentEntity("http://server:8080/exampleData?key=1", fragmentInfo(FRAGMENT_VALUE_3), List.of(), List.of());

        ldesFragmentEntityRepository.saveAll(List.of(ldesFragmentEntity_1, ldesFragmentEntity_2, ldesFragmentEntity_3));
        Optional<LdesFragment> ldesFragment = ldesFragmentMongoRepository.retrieveFragment(VIEW_SHORTNAME, PATH, FRAGMENT_VALUE_2);

        assertTrue(ldesFragment.isPresent());
        assertEquals(ldesFragmentEntity_2.toLdesFragment().getFragmentId(), ldesFragment.get().getFragmentId());
    }

    private FragmentInfo fragmentInfo(String fragmentValue) {
        return new FragmentInfo(VIEW, SHAPE, VIEW_SHORTNAME, PATH, fragmentValue);
    }


}
