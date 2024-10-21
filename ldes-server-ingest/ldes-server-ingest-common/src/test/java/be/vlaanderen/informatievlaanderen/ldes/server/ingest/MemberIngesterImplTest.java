package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.MemberMetricsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.collection.MemberExtractorCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.collection.MemberExtractorCollectionImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.VersionObjectMemberExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.metrics.IngestionMetricsService;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
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

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberIngesterImplTest {
    private static final String COLLECTION_NAME = "hindrances";
    private static final String MEMBER_SUBJECT = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/483";
    private static final String VERSION_OF = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622";
    private static final LocalDateTime TIMESTAMP = ZonedDateTime.parse("2020-12-28T09:36:37.127Z").toLocalDateTime();

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberIngestValidator validator;
    private MeterRegistry meterRegistry;
    private MemberIngester memberIngestService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        Metrics.globalRegistry.add(meterRegistry);

        MemberExtractorCollection memberExtractorCollection = new MemberExtractorCollectionImpl();
        IngestionMetricsService ingestionMetricsService = new IngestionMetricsService(mock(MemberMetricsRepository.class));
        memberIngestService = new MemberIngesterImpl(validator, memberRepository, memberExtractorCollection, ingestionMetricsService);

        final MemberExtractor memberExtractor = new VersionObjectMemberExtractor(COLLECTION_NAME, "http://purl.org/dc/terms/isVersionOf", "http://www.w3.org/ns/prov#generatedAtTime");
        memberExtractorCollection.addMemberExtractor(COLLECTION_NAME, memberExtractor);
    }

    @Test
    void whenValidatorThrowsAnException_thenTheIngestIsAborted_andTheExceptionIsThrown() {
        Model model = RDFParser.source("example-ldes-member.nq").lang(Lang.NQUADS).build().toModel();

        IngestedMember member = new IngestedMember(
                MEMBER_SUBJECT, COLLECTION_NAME,
                VERSION_OF, TIMESTAMP,
                true, "txId", model);

        doThrow(new RuntimeException("testException")).when(validator).validate(member);

        var exception = assertThrows(RuntimeException.class, () -> memberIngestService.ingest(COLLECTION_NAME, model));
        assertEquals("testException", exception.getMessage());
        var counter = meterRegistry.find(IngestionMetricsService.INGEST).counter();
        assertThat(counter).isNull();
        verifyNoInteractions(memberRepository);
    }

    @Test
    @DisplayName("Adding Member when there is a member with the same id that already exists")
    void when_TheMemberAlreadyExists_thenEmptyOptionalIsReturned() {
        Model model = RDFParser.source("example-ldes-member.nq").lang(Lang.NQUADS).build().toModel();
        IngestedMember member = new IngestedMember(
                MEMBER_SUBJECT, COLLECTION_NAME,
                VERSION_OF, TIMESTAMP,
                true, "txId", model);
        when(memberRepository.insertAll(List.of(member))).thenReturn(0);

        boolean memberIngested = memberIngestService.ingest(COLLECTION_NAME, model);

        assertThat(memberIngested).isFalse();
        var counter = meterRegistry.find(IngestionMetricsService.INGEST).counter();
        assertThat(counter).isNull();
        verify(memberRepository, times(1)).insertAll(List.of(member));
    }

    @Test
    @DisplayName("Adding Member when there is no existing member with the same id")
    void when_TheMemberDoesNotAlreadyExists_thenMemberIsStored() {
        Model model = RDFParser.source("example-ldes-member.nq").lang(Lang.NQ).toModel();
        IngestedMember member = new IngestedMember(
                MEMBER_SUBJECT, COLLECTION_NAME,
                VERSION_OF, TIMESTAMP,
                true, "txId", model);
        when(memberRepository.insertAll(List.of(member))).thenReturn(1);

        boolean memberIngested = memberIngestService.ingest(COLLECTION_NAME, model);

        assertThat(memberIngested).isTrue();
        Gauge counter = meterRegistry.find(IngestionMetricsService.INGEST).gauge();
        assertThat(counter).isNotNull();
        assertThat(counter.value()).isEqualTo(1);
        InOrder inOrder = inOrder(memberRepository);
        inOrder.verify(memberRepository, times(1)).insertAll(List.of(member));
        inOrder.verifyNoMoreInteractions();
    }

}