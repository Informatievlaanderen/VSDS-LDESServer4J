package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.collection.MemberExtractorCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.collection.MemberExtractorCollectionImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.VersionObjectMemberExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberIngesterImplTest {
    private static final String COLLECTION_NAME = "hindrances";
    private static final String MEMBER_ID = "hindrances/https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/483";
    private static final String VERSION_OF = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622";
    private static final LocalDateTime TIMESTAMP = ZonedDateTime.parse("2020-12-28T09:36:37.127Z").toLocalDateTime();

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private MemberIngestValidator validator;

    private MemberIngester memberIngestService;

    @BeforeEach
    void setUp() {
        MemberExtractorCollection memberExtractorCollection = new MemberExtractorCollectionImpl();
        memberIngestService = new MemberIngesterImpl(validator, memberRepository, eventPublisher, memberExtractorCollection);

        final MemberExtractor memberExtractor = new VersionObjectMemberExtractor(COLLECTION_NAME, "http://purl.org/dc/terms/isVersionOf", "http://www.w3.org/ns/prov#generatedAtTime");
        memberExtractorCollection.addMemberExtractor(COLLECTION_NAME, memberExtractor);
    }

    @Test
    void whenValidatorThrowsAnException_thenTheIngestIsAborted_andTheExceptionIsThrown() {
        Model model = RDFParser.source("example-ldes-member.nq").lang(Lang.NQUADS).build().toModel();

        Member member = new Member(
                MEMBER_ID, COLLECTION_NAME,
                "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622", TIMESTAMP,
                0L, "txId", model);

        doThrow(new RuntimeException("testException")).when(validator).validate(member);

        var exception = assertThrows(RuntimeException.class, () -> memberIngestService.ingest(COLLECTION_NAME, model));
        assertEquals("testException", exception.getMessage());
        verifyNoInteractions(memberRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("Adding Member when there is a member with the same id that already exists")
    void when_TheMemberAlreadyExists_thenEmptyOptionalIsReturned() throws IOException {
        Model model = RDFParser.source("example-ldes-member.nq").lang(Lang.NQUADS).build().toModel();
        Member member = new Member(
                MEMBER_ID, COLLECTION_NAME,
                "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622", TIMESTAMP,
                0L, "txId", model);
        when(memberRepository.insert(member)).thenReturn(Optional.empty());

        boolean memberIngested = memberIngestService.ingest(COLLECTION_NAME, model);

        assertThat(memberIngested).isFalse();
        verify(memberRepository, times(1)).insert(member);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("Adding Member when there is no existing member with the same id")
    void when_TheMemberDoesNotAlreadyExists_thenMemberIsStored() throws IOException {
        Model model = RDFParser.source("example-ldes-member.nq").lang(Lang.NQ).toModel();
        Member member = new Member(
                MEMBER_ID, COLLECTION_NAME,
                "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622", TIMESTAMP,
                0L, "txId", model);
        when(memberRepository.insert(member)).thenReturn(Optional.of(member));

        boolean memberIngested = memberIngestService.ingest(COLLECTION_NAME, model);

        assertThat(memberIngested).isTrue();
        InOrder inOrder = inOrder(memberRepository, eventPublisher);
        inOrder.verify(memberRepository, times(1)).insert(member);
        inOrder.verify(eventPublisher).publishEvent((MemberIngestedEvent) any());
        inOrder.verifyNoMoreInteractions();
    }

}