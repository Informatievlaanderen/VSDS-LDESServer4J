package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.services;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.entities.MemberEntityV1;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.entities.MemberEntityV2;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.valueobjects.EventStreamProperties;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberEntityV2BuilderTest {
    private static final String ID = "observations/https://telraam.net/en/location/9000001518#bike-2024-02-20T13:00:00+00:00";
    private static final String COLLECTION_NAME = "observations";
    private static final long SEQUENCE_NR = 1L;
    private static final String VERSION_OF = "https://telraam.net/en/location/9000001518#bike";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.parse("2024-02-20T13:00:00");
    private static final EventStreamProperties EVENT_STREAM_PROPERTIES = new EventStreamProperties("http://purl.org/dc/terms/isVersionOf", "http://www.w3.org/ns/prov#generatedAtTime");


    @Test
    void given_ValidMemberV1_when_BuildMemberV2_then_ReturnV2EquivalentToV1() throws IOException {
        final String model = String.join("\n", readFileContent());
        final MemberEntityV1 memberEntityV1 = new MemberEntityV1(ID, COLLECTION_NAME, SEQUENCE_NR, model);
        final MemberEntityV2 expectedMemberEntityV2 = new MemberEntityV2(ID, COLLECTION_NAME, VERSION_OF, TIMESTAMP, SEQUENCE_NR, "txId", model);

        final MemberEntityV2 actualMemberEntityV2 = MemberV2Builder.createWithEventStreamProperties(EVENT_STREAM_PROPERTIES).with(memberEntityV1).build();

        assertThat(actualMemberEntityV2).usingRecursiveComparison().ignoringFields("transactionId").as("All fields should be the same, except for the transaction id").isEqualTo(expectedMemberEntityV2);
    }

    @Test
    void given_MemberV1WithMissingVersionOf_when_BuildMemberV2_then_ReturnThrowException() throws IOException {
        final List<String> modelLines = readFileContent();
        modelLines.removeIf(line -> line.contains(EVENT_STREAM_PROPERTIES.versionOfPath()));
        final String model = String.join("\n", modelLines);
        final MemberEntityV1 memberEntityV1 = new MemberEntityV1(ID, COLLECTION_NAME, SEQUENCE_NR, model);

        MemberV2Builder builder = MemberV2Builder
                .createWithEventStreamProperties(EVENT_STREAM_PROPERTIES)
                .with(memberEntityV1);

        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Database model does not contain expected %s", EVENT_STREAM_PROPERTIES.versionOfPath());
    }

    @Test
    void given_MemberV1WithMissingTimestamp_when_BuildMemberV2_then_ReturnThrowException() throws IOException {
        final List<String> modelLines = readFileContent();
        modelLines.removeIf(line -> line.contains(EVENT_STREAM_PROPERTIES.timestampPath()));
        final String model = String.join("\n", modelLines);
        final MemberEntityV1 memberEntityV1 = new MemberEntityV1(ID, COLLECTION_NAME, SEQUENCE_NR, model);

        MemberV2Builder builder = MemberV2Builder
                .createWithEventStreamProperties(EVENT_STREAM_PROPERTIES)
                .with(memberEntityV1);

        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Database model does not contain expected %s", EVENT_STREAM_PROPERTIES.timestampPath());
    }

    private List<String> readFileContent() throws IOException {
        final File file = ResourceUtils.getFile("classpath:member.nq");
        return FileUtils.readLines(file, StandardCharsets.UTF_8);
    }
}