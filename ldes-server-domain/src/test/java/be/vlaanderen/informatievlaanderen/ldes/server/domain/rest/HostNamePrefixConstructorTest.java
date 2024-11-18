package be.vlaanderen.informatievlaanderen.ldes.server.domain.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HostNamePrefixConstructorTest {

    private final String hostname = "http://localhost:8080";
    private HostNamePrefixConstructor prefixConstructor;

    @BeforeEach
    void setUp() {
        prefixConstructor = new HostNamePrefixConstructor(hostname);
    }

    @Test
    void when_BuildPrefix_Then_ReturnHostname() {
        String prefix = prefixConstructor.buildPrefix();

        assertThat(prefix).isEqualTo(hostname);
    }

    @Test
    void given_CollectionUri_When_CreateUri_then_ReturnMap() {
        Map<String, String> result = prefixConstructor.buildCollectionUri("event-stream");

        assertThat(result)
                .containsEntry("event-stream", "http://localhost:8080/event-stream/");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/event-stream/timebased", "/event-stream/timebased?pageNumber=1"})
    void given_FragmentId_when_CreatePrefixes_then_ReturnValidMap(String fragmentId) {
        Map<String, String> result = prefixConstructor.buildFragmentUri("event-stream", fragmentId);

        assertThat(result)
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        "timebased", "http://localhost:8080/event-stream/timebased/",
                        "event-stream", "http://localhost:8080/event-stream/"
                ));
    }
}