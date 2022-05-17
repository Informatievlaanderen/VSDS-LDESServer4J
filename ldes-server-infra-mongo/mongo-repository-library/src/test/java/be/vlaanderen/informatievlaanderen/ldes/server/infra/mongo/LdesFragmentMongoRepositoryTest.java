package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.config.EndpointConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.services.LdesFragmentCreator;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LdesFragmentMongoRepositoryTest {
    private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
    private final LdesFragmentCreator ldesFragmentCreator = new LdesFragmentCreator(new EndpointConfig("localhost"));
    private final LdesMemberMongoRepository ldesFragmentMongoRepository = new LdesMemberMongoRepository(
            ldesFragmentRepository, ldesFragmentCreator);

    @DisplayName("Correct saving of an LdesFragment in MongoDB")
    @Test
    void when_LdesFragmentIsSavedInRepository_CreatedResourceIsReturned() {
        JSONObject originalLdesFragment = new JSONObject(Map.of("data", "some_ldes_data"));
        LdesFragmentEntity originalLdesFragmentEntity = new LdesFragmentEntity(originalLdesFragment.hashCode(),
                originalLdesFragment);
        JSONObject expectedStoredLdesFragment = new JSONObject(Map.of("data", "some_ldes_data_stored"));
        LdesFragmentEntity expectedLdesFragmentEntity = new LdesFragmentEntity(expectedStoredLdesFragment.hashCode(),
                expectedStoredLdesFragment);
        when(ldesFragmentRepository.save(any())).thenReturn(expectedLdesFragmentEntity);

        JSONObject actualStoredLdesFragment = ldesFragmentMongoRepository.saveLdesMember(originalLdesFragment);

        assertEquals(expectedStoredLdesFragment, actualStoredLdesFragment);
        verify(ldesFragmentRepository, times(1)).save(any());
    }

    @DisplayName("Correct retrieval of an LdesFragment from MongoDB")
    @Test
    void when_RepositoryIsQueried_LdesFragmentsPageIsReturned() {
        JSONObject storedLdesFragment = new JSONObject(Map.of("data", "some_ldes_data_stored"));
        LdesFragmentEntity storedLdesFragmentEntity = new LdesFragmentEntity(storedLdesFragment.hashCode(),
                storedLdesFragment);
        PageImpl<LdesFragmentEntity> pageStoredLdesFragmentEntity = new PageImpl<>(List.of(storedLdesFragmentEntity),
                Pageable.ofSize(1), 5);
        when(ldesFragmentRepository.findAll(PageRequest.of(0, 1))).thenReturn(pageStoredLdesFragmentEntity);
        JSONObject expectedReturnedLdesFragment = ldesFragmentCreator
                .createLdesFragmentPage(pageStoredLdesFragmentEntity);

        JSONObject actualRetrievedLdesFragment = ldesFragmentMongoRepository.retrieveLdesFragmentsPage(0);

        assertEquals(expectedReturnedLdesFragment, actualRetrievedLdesFragment);
        verify(ldesFragmentRepository, times(1)).findAll(PageRequest.of(0, 1));
    }
}