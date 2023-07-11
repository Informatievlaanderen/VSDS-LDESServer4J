package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class MemberIngestServiceImplTest {

	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private MemberIngestServiceImpl memberIngestService;

	@BeforeEach
	void setUp() {
		memberIngestService = new MemberIngestServiceImpl(memberRepository);
	}

	@Test
	void when_EventStreamIsDeleted_then_DeleteAllMembers() throws IOException {
		final String collection = "collectionName";
		final String memberId = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1";
		final String ldesMemberString = FileUtils.readFileToString(
				ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		final Member member = new Member(
				memberId, collection, 0L, null, null, RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS),
				List.of());
		when(memberRepository.getMemberStreamOfCollection(collection)).thenReturn(Stream.of(member));
		((MemberIngestServiceImpl) memberIngestService)
				.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(collection));
		verify(memberRepository).deleteMembersByCollection(collection);
		verifyNoMoreInteractions(memberRepository);
	}
}
