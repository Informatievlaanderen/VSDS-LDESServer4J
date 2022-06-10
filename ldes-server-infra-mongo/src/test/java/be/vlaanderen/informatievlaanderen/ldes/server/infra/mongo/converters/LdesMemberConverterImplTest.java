package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
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

    private LdesMember ldesMember;
    private LdesMemberEntity ldesMemberEntity;

    @BeforeEach
    public void init() throws IOException, ParseException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        // ldesMember = readLdesMemberFromFile(classLoader, "example-ldes-member.txt");
        // ldesMemberEntity = readLdesMemberEntityFromFile(classLoader, "example-ldes-member-entity.json");
    }

    @Test
    @DisplayName("Convert LdesMember to LdesMemberEntity")
    void toEntity() {
        // LdesMemberEntity actualLdesMemberEntity = new LdesMemberConverterImpl().toEntity(ldesMember);
        // assertEquals(ldesMemberEntity.getLdesMember().keySet(), actualLdesMemberEntity.getLdesMember().keySet());
    }

    @Test
    @DisplayName("Convert LdesMemberEntity to LdesMember")
    void fromEntity() {
        // LdesMember actualLdesMember = new LdesMemberConverterImpl().fromEntity(ldesMemberEntity);
        // assertEquals(ldesMember.getQuads().length, actualLdesMember.getQuads().length);
    }

    // private LdesMemberEntity readLdesMemberEntityFromFile(ClassLoader classLoader, String fileName)
    // throws IOException, ParseException, URISyntaxException {
    // JSONObject jsonObject = (JSONObject) new JSONParser().parse(
    // Files.newBufferedReader(Paths.get(Objects.requireNonNull(classLoader.getResource(fileName)).toURI())));
    // return new LdesMemberEntity(jsonObject.hashCode(), jsonObject);
    // }
    //
    // private LdesMember readLdesMemberFromFile(ClassLoader classLoader, String fileName)
    // throws URISyntaxException, IOException {
    // File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
    // return new LdesMember(Files.lines(Paths.get(file.toURI())).toArray(String[]::new));
    // }
}