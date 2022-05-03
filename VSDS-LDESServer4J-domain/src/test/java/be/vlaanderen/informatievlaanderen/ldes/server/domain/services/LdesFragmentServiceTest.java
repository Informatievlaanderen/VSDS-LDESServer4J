package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LdesFragmentServiceTest {

    private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);

    @DisplayName("Correct storing of an LdesFragment in a repository")
    @Test
    void when_LdesFragmentIsStoredInRepository_CreatedResourceIsReturned(){
        JSONObject originalLdesFragment = new JSONObject(Map.of("data", "some_ldes_data"));
        JSONObject expectedStoredLdesFragment = new JSONObject(Map.of("data", "some_ldes_data_stored"));
        when(ldesFragmentRepository.saveLdesFragment(originalLdesFragment)).thenReturn(expectedStoredLdesFragment);

        JSONObject actualStoredLdesFragment = ldesFragmentRepository.saveLdesFragment(originalLdesFragment);

        assertEquals(expectedStoredLdesFragment, actualStoredLdesFragment);
        verify(ldesFragmentRepository, times(1)).saveLdesFragment(originalLdesFragment);
    }

}