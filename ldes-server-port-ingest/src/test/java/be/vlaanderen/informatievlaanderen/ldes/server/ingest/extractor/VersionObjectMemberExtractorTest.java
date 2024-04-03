package be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.exceptions.MemberIdNotFoundException;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

class VersionObjectMemberExtractorTest {
    private static final String COLLECTION = "mobility-hindrances";
    private static final String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
    private static final String TIMESTAMP_PATH = "http://www.w3.org/ns/prov#generatedAtTime";
    private MemberExtractor memberExtractor;

    @BeforeEach
    void setUp() {
        memberExtractor = new VersionObjectMemberExtractor(COLLECTION, VERSION_OF_PATH, TIMESTAMP_PATH);
    }

    @Test
    void given_ValidModel_when_ExtractMembers_then_ReturnSingleMember() throws IOException {
        final Model model = readValidModel();
        final String memberId = "%s/%s".formatted(COLLECTION, "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/483");
        final Member expectedMember = new Member(
                memberId,
                COLLECTION,
                "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622",
                ZonedDateTime.parse("2020-12-28T09:36:37.127Z").toLocalDateTime(),
                null,
                "txId",
                model
        );

        final Member member = memberExtractor.extractMembers(model).getFirst();

        assertThat(member)
                .usingRecursiveComparison()
                .ignoringFields("model", "transactionId")
                .isEqualTo(expectedMember);
    }

    @Test
    void given_ValidModelThatContainsBlankNodesWithVersionOf_when_ExtractMembers_then_ThrowNoException() {
        final Model model = RDFParser.source("member-with-nested-isversionof-bnodes.ttl").toModel();

        assertThatNoException().isThrownBy(() -> memberExtractor.extractMembers(model));
    }

    @Test
    void given_ModelWithTwoVersionOf_when_ExtractMembers_then_ThrowException() throws IOException {
        final Model model = readModelWithTwoVersionOfPaths();

        assertThatThrownBy(() -> memberExtractor.extractMembers(model))
                .isInstanceOf(MemberIdNotFoundException.class);
    }

    @Test
    void given_ModelWithoutVersionOfPath_when_ExtractMembers_then_ThrowException() throws IOException {
        final Model model = readModelWithoutSpecifiedPath(VERSION_OF_PATH);

        assertThatThrownBy(() -> memberExtractor.extractMembers(model))
                .isInstanceOf(MemberIdNotFoundException.class);
    }

    @Test
    void given_ModelWithoutTimestampPath_when_ExtractMembers_then_ThrowException() throws IOException {
        final Model model = readModelWithoutSpecifiedPath(TIMESTAMP_PATH);

        assertThatThrownBy(() -> memberExtractor.extractMembers(model))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ingested model does not contain expected %s", TIMESTAMP_PATH);
    }

    private Model readModelWithTwoVersionOfPaths() throws IOException {
        final List<String> lines = readContentFromFile();
        lines.add("<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/484> <http://purl.org/dc/terms/isVersionOf> <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622> .\n");
        final String modelString = String.join("\n", lines);
        return RDFParser.fromString(modelString).lang(Lang.NQ).toModel();
    }

    private Model readModelWithoutSpecifiedPath(String path) throws IOException {
        final String modelString = readContentFromFile().stream()
                .filter(line -> !line.contains(path))
                .collect(Collectors.joining("\n"));
        return RDFParser.fromString(modelString).lang(Lang.NQ).toModel();
    }

    private Model readValidModel() throws IOException {
        final String modelString = String.join("\n", readContentFromFile());
        return RDFParser.fromString(modelString).lang(Lang.NQ).toModel();
    }

    private List<String> readContentFromFile() throws IOException {
        final File file = ResourceUtils.getFile("classpath:example-ldes-member.nq");
        return FileUtils.readLines(file, StandardCharsets.UTF_8);
    }

}