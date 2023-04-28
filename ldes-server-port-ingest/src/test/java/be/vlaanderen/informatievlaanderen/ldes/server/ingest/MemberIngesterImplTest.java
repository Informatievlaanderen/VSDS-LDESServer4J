package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.events.MemberIngestedEvent;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberIngesterImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MemberIngesterImpl memberIngestService;

    @Test
    @DisplayName("Adding Member when there is a member with the same id that already exists")
    void when_TheMemberAlreadyExists_thenMemberIsReturned() throws IOException {
        String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
                StandardCharsets.UTF_8);
        Member member = new Member(
                "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1", "collectionName",
                0L, RDFParser.fromString(ldesMemberString).lang(Lang.NQUADS).build().toModel());
        when(memberRepository.memberExists(member.getId())).thenReturn(true);

        memberIngestService.ingest(member);

        InOrder inOrder = inOrder(memberRepository, eventPublisher);
        inOrder.verify(memberRepository,
                times(1)).memberExists(member.getId());
        inOrder.verifyNoMoreInteractions();
        verifyNoInteractions(eventPublisher);
        verifyNoMoreInteractions(memberRepository);
    }

    @Test
    @DisplayName("Adding Member when there is no existing member with the same id")
    void when_TheMemberDoesNotAlreadyExists_thenMemberIsStored() throws IOException {
        String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
                StandardCharsets.UTF_8);
        Member member = new Member(
                "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1", "collectionName",
                0L, RDFParser.fromString(ldesMemberString).lang(Lang.NQUADS).build().toModel());
        when(memberRepository.memberExists(member.getId())).thenReturn(false);

        memberIngestService.ingest(member);

        InOrder inOrder = inOrder(memberRepository, eventPublisher);
        inOrder.verify(memberRepository, times(1)).memberExists(member.getId());
        inOrder.verify(memberRepository, times(1)).saveMember(member);
        inOrder.verify(eventPublisher).publishEvent((MemberIngestedEvent) any());
        inOrder.verifyNoMoreInteractions();
    }
  
}