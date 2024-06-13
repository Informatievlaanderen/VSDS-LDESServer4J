package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberFetcherImplTest {
    private static final String COLLECTION = "test-collection";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final String TIMESTAMP_PATH = "http://purl.org/dc/terms/created";
    private static final String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
    private final List<String> MEMBER_IDS = Stream
            .of("http://example.org/observation/1", "http://example.org/measurements/2")
            .map(id -> "%s/%s".formatted(COLLECTION, id))
            .toList();

    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MemberFetcherImpl memberFetcher;

    @Test
    void given_StateObjectEventStream_test_FetchAllByIds() {
        when(memberRepository.findAllByIds(MEMBER_IDS)).thenReturn(createIngestMembers());

        memberFetcher.handleEventStreamCreatedEvent(new EventStreamCreatedEvent(new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, true)));

        assertThat(memberFetcher.fetchAllByIds(MEMBER_IDS))
                .as("The empty models in the members need to have two additional statements added")
                .allMatch(member -> member.model().size() == 2)
                .map(be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member::id)
                .containsExactlyInAnyOrderElementsOf(MEMBER_IDS);
    }

    @Test
    void given_VersionObjectEventStream_test_FetchAllByIds() {
        when(memberRepository.findAllByIds(MEMBER_IDS)).thenReturn(createIngestMembers());

        memberFetcher.handleEventStreamCreatedEvent(new EventStreamCreatedEvent(new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, false)));

        assertThat(memberFetcher.fetchAllByIds(MEMBER_IDS))
                .as("The empty models in the members should not be altered")
                .allMatch(member -> member.model().isEmpty())
                .map(be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member::id)
                .containsExactlyInAnyOrderElementsOf(MEMBER_IDS);
    }

    private Stream<IngestedMember> createIngestMembers() {
        return MEMBER_IDS.stream()
                .map(id -> new IngestedMember(
                        id,
                        COLLECTION,
                        "http://example.org/verkeerspunt/meting",
                        TIMESTAMP,
                        null,
                        true,
                        "txId",
                        ModelFactory.createDefaultModel()));
    }

}