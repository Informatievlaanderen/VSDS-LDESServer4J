package be.vlaanderen.informatievlaanderen.ldes.server.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import org.json.simple.JSONArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LdesMemberConverterImplTest {

    LdesMemberConverter ldesMemberConverter = new LdesMemberConverterImpl();

    @Test
    @DisplayName("Correct conversion of LdesMember to JSONArray")
    void when_LdesMemberIsProvided_LdesMemberConverterReturnsJsonArray() throws URISyntaxException, IOException {
        LdesMember ldesMember = readLdesMemberFromFile("example-ldes-member.txt");
        JSONArray jsonArray = ldesMemberConverter.convertLdesMemberToJSONArray(ldesMember);
        assertEquals(12, jsonArray.size());
    }

    private LdesMember readLdesMemberFromFile(String fileName) throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return new LdesMember(Files.lines(Paths.get(file.toURI())).toArray(String[]::new));
    }

}