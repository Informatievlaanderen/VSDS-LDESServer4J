package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import org.junit.jupiter.api.Test;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.FragmentIdConverter.toFragmentId;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FragmentIdConverterTest {
    @Test
    void when_FragmentIdConverter_ToFragmentId_ExpectCorrectFormat() {
        String fragmentId = toFragmentId("http://localhost", "exampleData", "https://w3id.org/ldes#timestampPath", "2020-12-05T09:00:00.000Z");

        String expectedFragmentId = "http://localhost/exampleData?timestampPath=2020-12-05T09:00:00.000Z";

        assertEquals(expectedFragmentId, fragmentId);
    }
}
