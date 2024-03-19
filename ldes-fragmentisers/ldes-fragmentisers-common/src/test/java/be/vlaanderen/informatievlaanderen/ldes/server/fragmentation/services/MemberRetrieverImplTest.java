package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.membermapper.MemberMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.membermapper.MemberMapperCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.EventSourceService;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberRetrieverImplTest {
    private static final String MEMBER_ID = "collection/http://example.org/member-id";
    private static final String COLLECTION_NAME = "collection";
    private static final String VERSION_OF = "version_of";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final long LAST_PROCESSED_SEQUENCE_NR = 65L;

    @Mock
    private EventSourceService eventSourceService;
    @Mock
    private MemberMapperCollection memberMapperCollection;
    @InjectMocks
    private MemberRetrieverImpl memberRetriever;

    @Test
    void given_MemberMapper_and_IngestMember_when_FindFirst_then_Return_FragmentMember() {
        final MemberMapper memberMapper = new MemberMapper("http://purl.org/dc/terms/isVersionOf", "http://www.w3.org/ns/prov#generatedAtTime");
        when(eventSourceService.findFirstByCollectionNameAndSequenceNrGreaterThan(COLLECTION_NAME, LAST_PROCESSED_SEQUENCE_NR))
                .thenReturn(Optional.of(new Member(MEMBER_ID, COLLECTION_NAME, VERSION_OF, TIMESTAMP, LAST_PROCESSED_SEQUENCE_NR + 1, "txId", ModelFactory.createDefaultModel())));
        when(memberMapperCollection.getMemberMapper(COLLECTION_NAME)).thenReturn(Optional.of(memberMapper));

        var retrievedMember = memberRetriever.findFirstByCollectionNameAndSequenceNrGreaterThan(COLLECTION_NAME, LAST_PROCESSED_SEQUENCE_NR);

        assertThat(retrievedMember)
                .hasValueSatisfying(member -> {
                    assertThat(member.id()).isEqualTo(MEMBER_ID);
                    assertThat(member.sequenceNr()).isEqualTo(LAST_PROCESSED_SEQUENCE_NR + 1);
                    assertThat(member.model().size()).isEqualTo(2);
                });
    }

    @Test
    void given_NoMemberMapper_when_FindFirst_then_ThrowException() {
        assertThatThrownBy(() -> memberRetriever.findFirstByCollectionNameAndSequenceNrGreaterThan(COLLECTION_NAME, LAST_PROCESSED_SEQUENCE_NR))
                .isInstanceOf(MissingResourceException.class);
    }

    @Test
    void given_NoIngestMember_when_FindFirst_then_ReturnEmptyOptional() {
        final MemberMapper memberMapper = new MemberMapper("http://purl.org/dc/terms/isVersionOf", "http://www.w3.org/ns/prov#generatedAtTime");
        when(memberMapperCollection.getMemberMapper(COLLECTION_NAME)).thenReturn(Optional.of(memberMapper));

        var retrievedMember = memberRetriever.findFirstByCollectionNameAndSequenceNrGreaterThan(COLLECTION_NAME, LAST_PROCESSED_SEQUENCE_NR);

        assertThat(retrievedMember).isEmpty();
    }
}