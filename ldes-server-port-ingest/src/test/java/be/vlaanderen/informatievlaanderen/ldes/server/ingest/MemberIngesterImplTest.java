package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberIngesterImplTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private MemberIngestValidator validator;

	@InjectMocks
	private MemberIngesterImpl memberIngestService;

	@Test
	void whenValidatorThrowsAnException_thenTheIngestIsAborted_andTheExceptionIsThrown() {
		Model model = RDFParser.source("example-ldes-member.nq").lang(Lang.NQUADS).build().toModel();

		Member member = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1", "collectionName",
				0L, model);

		doThrow(new RuntimeException("testException")).when(validator).validate(member);

		var exception = assertThrows(RuntimeException.class, () -> memberIngestService.ingest(member));
		assertEquals("testException", exception.getMessage());
		verifyNoInteractions(memberRepository);
		verifyNoInteractions(eventPublisher);
	}

	@Test
	@DisplayName("Adding Member when there is a member with the same id that already exists")
	void when_TheMemberAlreadyExists_thenEmptyOptionalIsReturned() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		Member member = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1", "collectionName",
				0L, RDFParser.fromString(ldesMemberString).lang(Lang.NQUADS).build().toModel());
		when(memberRepository.insert(member)).thenReturn(Optional.empty());

		boolean memberIngested = memberIngestService.ingest(member);

		assertThat(memberIngested).isFalse();
		verify(memberRepository, times(1)).insert(member);
		verifyNoInteractions(eventPublisher);
	}

	@Test
	@DisplayName("Adding Member when there is no existing member with the same id")
	void when_TheMemberDoesNotAlreadyExists_thenMemberIsStored() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		Member member = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1", "collectionName",
				0L, RDFParser.fromString(ldesMemberString).lang(Lang.NQUADS).build().toModel());
		when(memberRepository.insert(member)).thenReturn(Optional.of(member));

		boolean memberIngested = memberIngestService.ingest(member);

		assertThat(memberIngested).isTrue();
		InOrder inOrder = inOrder(memberRepository, eventPublisher);
		inOrder.verify(memberRepository, times(1)).insert(member);
		inOrder.verify(eventPublisher).publishEvent((MemberIngestedEvent) any());
		inOrder.verifyNoMoreInteractions();
	}

}