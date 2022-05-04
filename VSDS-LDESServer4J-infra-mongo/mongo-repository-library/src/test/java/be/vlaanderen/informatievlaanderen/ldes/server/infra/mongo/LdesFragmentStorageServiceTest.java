package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.config.EndpointConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entity.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repository.LdesFragmentMongoRepository;
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

class LdesFragmentStorageServiceTest {
    private final LdesFragmentMongoRepository ldesFragmentMongoRepository = mock(LdesFragmentMongoRepository.class);
    private final LdesFragmentCreator ldesFragmentCreator = new LdesFragmentCreator(new EndpointConfig("localhost"));
    private final LdesFragmentStorageService ldesFragmentStorageService = new LdesFragmentStorageService(
            ldesFragmentMongoRepository, ldesFragmentCreator);

    @DisplayName("Correct saving of an LdesFragment in MongoDB")
    @Test
    void when_LdesFragmentIsSavedInRepository_CreatedResourceIsReturned() {
        JSONObject originalLdesFragment = new JSONObject(Map.of("data", "some_ldes_data"));
        LdesFragmentEntity originalLdesFragmentEntity = new LdesFragmentEntity(originalLdesFragment.hashCode(),
                originalLdesFragment);
        JSONObject expectedStoredLdesFragment = new JSONObject(Map.of("data", "some_ldes_data_stored"));
        LdesFragmentEntity expectedLdesFragmentEntity = new LdesFragmentEntity(expectedStoredLdesFragment.hashCode(),
                expectedStoredLdesFragment);
        when(ldesFragmentMongoRepository.save(any())).thenReturn(expectedLdesFragmentEntity);

        JSONObject actualStoredLdesFragment = ldesFragmentStorageService.saveLdesFragment(originalLdesFragment);

        assertEquals(expectedStoredLdesFragment, actualStoredLdesFragment);
        verify(ldesFragmentMongoRepository, times(1)).save(any());
    }

    @DisplayName("Correct retrieval of an LdesFragment from MongoDB")
    @Test
    void when_RepositoryIsQueried_LdesFragmentsPageIsReturned() {
        JSONObject storedLdesFragment = new JSONObject(Map.of("data", "some_ldes_data_stored"));
        LdesFragmentEntity storedLdesFragmentEntity = new LdesFragmentEntity(storedLdesFragment.hashCode(),
                storedLdesFragment);
        PageImpl<LdesFragmentEntity> pageStoredLdesFragmentEntity = new PageImpl<>(List.of(storedLdesFragmentEntity),
                Pageable.ofSize(1), 5);
        when(ldesFragmentMongoRepository.findAll(PageRequest.of(0, 1))).thenReturn(pageStoredLdesFragmentEntity);
        JSONObject expectedReturnedLdesFragment = ldesFragmentCreator
                .createLdesFragmentPage(pageStoredLdesFragmentEntity);

        JSONObject actualRetrievedLdesFragment = ldesFragmentStorageService.retrieveLdesFragmentsPage(0);

        assertEquals(expectedReturnedLdesFragment, actualRetrievedLdesFragment);
        verify(ldesFragmentMongoRepository, times(1)).findAll(PageRequest.of(0, 1));
    }
}