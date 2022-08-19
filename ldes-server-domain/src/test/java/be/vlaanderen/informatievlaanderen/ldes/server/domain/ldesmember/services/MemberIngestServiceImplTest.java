package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationQueueMediator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
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

	private final LdesMemberRepository ldesMemberRepository = mock(LdesMemberRepository.class);

	private final FragmentationQueueMediator fragmentationQueueMediator = mock(FragmentationQueueMediator.class);
	private MemberIngestService memberIngestService;

	@BeforeEach
	void setUp() {
		memberIngestService = new MemberIngestServiceImpl(ldesMemberRepository, fragmentationQueueMediator);
	}

	@Test
	@DisplayName("Adding Member when there is a member with the same id that already exists")
	void when_TheMemberAlreadyExists_thenMemberIsReturned() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		LdesMember ldesMember = new LdesMember(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
		LdesMember savedMember = new LdesMember(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
		when(ldesMemberRepository.getLdesMemberById(ldesMember.getLdesMemberId())).thenReturn(Optional.of(savedMember));

		memberIngestService.addMember(ldesMember);

		InOrder inOrder = inOrder(ldesMemberRepository, fragmentationQueueMediator);
		inOrder.verify(ldesMemberRepository, times(1)).getLdesMemberById(ldesMember.getLdesMemberId());
		inOrder.verify(fragmentationQueueMediator, times(1)).addLdesMember(ldesMember.getLdesMemberId());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Adding Member when there is no existing member with the same id")
	void when_TheMemberDoesNotAlreadyExists_thenMemberIsStored() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		LdesMember ldesMember = new LdesMember(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
		LdesMember savedLdesMember = new LdesMember(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
		when(ldesMemberRepository.getLdesMemberById(ldesMember.getLdesMemberId())).thenReturn(Optional.empty());
		when(ldesMemberRepository.saveLdesMember(ldesMember)).thenReturn(savedLdesMember);

		memberIngestService.addMember(ldesMember);

		InOrder inOrder = inOrder(ldesMemberRepository, fragmentationQueueMediator);
		inOrder.verify(ldesMemberRepository, times(1)).getLdesMemberById(ldesMember.getLdesMemberId());
		inOrder.verify(ldesMemberRepository, times(1)).saveLdesMember(ldesMember);
		inOrder.verify(fragmentationQueueMediator, times(1)).addLdesMember(ldesMember.getLdesMemberId());
		inOrder.verifyNoMoreInteractions();
	}

	public void test() {

	}
}