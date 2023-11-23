package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
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
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberIngesterImplTest {

	public static final String DUPLICATE_MEMBER_INGESTED_MEMBER_WITH_ID_ALREADY_EXISTS = "Duplicate member ingested. Member with id {} already exists";
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
	void when_TheMemberAlreadyExists_thenFalseIsReturned() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		Member member = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1", "collectionName",
				0L, RDFParser.fromString(ldesMemberString).lang(Lang.NQUADS).build().toModel());
		when(memberRepository.insertMember(member)).thenReturn(FALSE);

		Logger logger = (Logger) LoggerFactory.getLogger(MemberIngesterImpl.class);
		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.start();
		logger.addAppender(listAppender);

		memberIngestService.ingest(member);

		List<ILoggingEvent> logsList = listAppender.list;

		assertEquals(DUPLICATE_MEMBER_INGESTED_MEMBER_WITH_ID_ALREADY_EXISTS, logsList.get(0).getMessage());
		assertEquals(Level.WARN, logsList.get(0).getLevel());
		verify(memberRepository, times(1)).insertMember(member);
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
		when(memberRepository.insertMember(member)).thenReturn(TRUE);

		memberIngestService.ingest(member);

		InOrder inOrder = inOrder(memberRepository, eventPublisher);
		inOrder.verify(memberRepository, times(1)).insertMember(member);
		inOrder.verify(eventPublisher).publishEvent((MemberIngestedEvent) any());
		inOrder.verifyNoMoreInteractions();
	}

}