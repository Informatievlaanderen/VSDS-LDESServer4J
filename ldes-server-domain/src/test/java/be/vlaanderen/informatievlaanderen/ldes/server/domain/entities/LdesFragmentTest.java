package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LdesFragmentTest {
    private static final String SHAPE = "https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape";
    private static final String HOSTNAME = "http://localhost:8080";
    private static final String COLLECTION_NAME = "mobility-hindrances";
    private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
    private static final String FRAGMENT_ID = "http://localhost:8080/mobility-hindrances?generatedAtTime=" + FRAGMENTATION_VALUE_1;
    private static final String TIMESTAMP_PATH = "http://www.w3.org/ns/prov#generatedAtTime";

    @Test
    @DisplayName("Test if fragment is empty or real")
    void when_ValueIsNULL_FragmentDoesNotReallyExist() {
        LdesFragment emptyFragment = new LdesFragment(FRAGMENT_ID, new FragmentInfo(String.format("%s/%s", HOSTNAME, COLLECTION_NAME), SHAPE, COLLECTION_NAME, List.of()));
        assertFalse(emptyFragment.isExistingFragment());
        LdesFragment realFragment = new LdesFragment(FRAGMENT_ID, new FragmentInfo(String.format("%s/%s", HOSTNAME, COLLECTION_NAME), SHAPE, COLLECTION_NAME, List.of(new FragmentPair(TIMESTAMP_PATH, FRAGMENTATION_VALUE_1))));
        assertTrue(realFragment.isExistingFragment());
    }

    @Test
    @DisplayName("Test if fragment is immutable or not")
    void when_LdesFragmentIsImmutable_IsImmutableReturnsTrue() {
        LdesFragment ldesFragment = new LdesFragment(FRAGMENT_ID, new FragmentInfo(String.format("%s/%s", HOSTNAME, COLLECTION_NAME), SHAPE, COLLECTION_NAME, List.of(new FragmentPair(TIMESTAMP_PATH, FRAGMENTATION_VALUE_1))));
        assertFalse(ldesFragment.isImmutable());
        ldesFragment.setImmutable(true);
        assertTrue(ldesFragment.isImmutable());
    }
}