package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entity.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repository.LdesFragmentMongoRepository;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LdesFragmentStorageServiceTest {
    private final LdesFragmentMongoRepository ldesFragmentMongoRepository = mock(LdesFragmentMongoRepository.class);

    @DisplayName("Correct saving of an LdesFragment in MongoDB")
    @Test
    void when_LdesFragmentIsSavedInRepository_CreatedResourceIsReturned() {
        JSONObject originalLdesFragment = new JSONObject(Map.of("data", "some_ldes_data"));
        LdesFragmentEntity originalLdesFragmentEntity = new LdesFragmentEntity(originalLdesFragment.hashCode(), originalLdesFragment);
        JSONObject expectedStoredLdesFragment = new JSONObject(Map.of("data", "some_ldes_data_stored"));
        LdesFragmentEntity expectedLdesFragmentEntity = new LdesFragmentEntity(expectedStoredLdesFragment.hashCode(), expectedStoredLdesFragment);
        when(ldesFragmentMongoRepository.save(originalLdesFragmentEntity))
                .thenReturn(expectedLdesFragmentEntity);

        LdesFragmentEntity actualStoredLdesFragment = ldesFragmentMongoRepository.save(originalLdesFragmentEntity);

        assertEquals(expectedLdesFragmentEntity, actualStoredLdesFragment);
        verify(ldesFragmentMongoRepository, times(1)).save(originalLdesFragmentEntity);
    }
}