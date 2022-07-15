package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.FragmentIdConverter.toFragmentId;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FragmentIdConverterTest {
    @Test
    void when_FragmentIdConverter_ToFragmentId_ExpectCorrectFormat() {
        String fragmentId = toFragmentId("http://localhost", "exampleData", List.of(new FragmentPair("timestampPath", "2020-12-05T09:00:00.000Z")));

        String expectedFragmentId = "http://localhost/exampleData?timestampPath=2020-12-05T09:00:00.000Z";

        assertEquals(expectedFragmentId, fragmentId);
    }
}
