package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.FragmentIdConverter;
import org.junit.jupiter.api.Test;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.FragmentIdConverter.getViewFromFragmentId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FragmentIdConverterTest {
    @Test
    void testFragmentPatternAgainstFragmentId() {
        assertEquals("https://testserver.com/objects",
                getViewFromFragmentId("https://testserver.com/objects?generatedAtTime=2022-06-15"));
        assertEquals("generatedAtTime",
                FragmentIdConverter.getPathFromFragmentId("https://testserver.com/objects?generatedAtTime=2022-06-15"));
        assertEquals("2022-06-15", FragmentIdConverter
                .getValueFromFragmentId("https://testserver.com/objects?generatedAtTime=2022-06-15"));
    }

    @Test
    void testFragmentPatternAgainstView() {
        assertEquals("https://testserver.com/objects", getViewFromFragmentId("https://testserver.com/objects"));
        assertNull(FragmentIdConverter.getPathFromFragmentId("https://testserver.com/objects"));
        assertNull(FragmentIdConverter.getValueFromFragmentId("https://testserver.com/objects"));
    }
}
