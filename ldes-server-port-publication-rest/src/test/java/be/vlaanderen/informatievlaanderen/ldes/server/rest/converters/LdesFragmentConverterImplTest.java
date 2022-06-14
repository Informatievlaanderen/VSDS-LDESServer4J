package be.vlaanderen.informatievlaanderen.ldes.server.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.rest.converters.LdesFragmentConverter.outputLdesFragment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LdesFragmentConverterImplTest {

    private final LdesFragmentConfig ldesFragmentConfig = new LdesFragmentConfig();

    @BeforeEach
    void setup() {
        ldesFragmentConfig.setView("http://localhost:8089/exampleData");
        ldesFragmentConfig.setShape("http://localhost:8089/exampleData/shape");
    }

    @Test
    @DisplayName("Correct conversion of LdesMember to JSONArray")
    void when_LdesMemberIsProvided_LdesMemberConverterReturnsJsonArray() throws URISyntaxException, IOException {
        LdesMember ldesMember = readLdesMemberFromFile("example-ldes-member.nq");
        LdesFragment ldesFragment = new LdesFragment(List.of(ldesMember), ldesFragmentConfig.toMap());
        String fragmentString = outputLdesFragment(ldesFragment, RDFFormat.NQUADS);

        String expectedFragment = getContentFromFile("example-ldes-fragment.nq");

        Model fragmentModel = RDFParserBuilder.create()
                .fromString(fragmentString)
                .lang(Lang.NQUADS)
                .toModel();

        Model expectedFragmentModel = RDFParserBuilder.create()
                .fromString(expectedFragment)
                .lang(Lang.NQUADS)
                .toModel();


        assertTrue(expectedFragmentModel.isIsomorphicWith(fragmentModel));
    }

    private LdesMember readLdesMemberFromFile(String fileName) throws URISyntaxException, IOException {
        return new LdesMember(getContentFromFile(fileName), Lang.NQUADS);
    }

    private String getContentFromFile(String fileName) throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
    }

}