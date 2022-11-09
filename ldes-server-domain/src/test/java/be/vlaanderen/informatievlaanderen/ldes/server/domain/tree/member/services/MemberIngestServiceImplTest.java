package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.Mockito.*;

class MemberIngestServiceImplTest {

	private final MemberRepository memberRepository = mock(MemberRepository.class);

	private final FragmentationMediator fragmentationMediator = mock(FragmentationMediator.class);
	private MemberIngestService memberIngestService;

	@BeforeEach
	void setUp() {
		memberIngestService = new MemberIngestServiceImpl(memberRepository, fragmentationMediator);
	}

	@Test
	@DisplayName("Adding Member when there is a member with the same id that already exists")
	void when_TheMemberAlreadyExists_thenMemberIsReturned() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		Member member = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
		Member savedMember = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
		when(memberRepository.getLdesMemberById(member.getLdesMemberId())).thenReturn(Optional.of(savedMember));

		memberIngestService.addMember(member);

		InOrder inOrder = inOrder(memberRepository, fragmentationMediator);
		inOrder.verify(memberRepository, times(1)).getLdesMemberById(member.getLdesMemberId());
		inOrder.verifyNoMoreInteractions();
		verifyNoInteractions(fragmentationMediator);
	}

	@Test
	@DisplayName("Adding Member when there is no existing member with the same id")
	void when_TheMemberDoesNotAlreadyExists_thenMemberIsStored() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		Member member = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
		Member savedMember = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
		when(memberRepository.getLdesMemberById(member.getLdesMemberId())).thenReturn(Optional.empty());
		when(memberRepository.saveLdesMember(member)).thenReturn(savedMember);

		memberIngestService.addMember(member);

		InOrder inOrder = inOrder(memberRepository, fragmentationMediator);
		inOrder.verify(memberRepository, times(1)).getLdesMemberById(member.getLdesMemberId());
		inOrder.verify(memberRepository, times(1)).saveLdesMember(member);
		inOrder.verify(fragmentationMediator, times(1)).addMemberToFragment(savedMember);
		inOrder.verifyNoMoreInteractions();
	}
}