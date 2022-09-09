package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.TracerMockHelper.mockTracer;
import static org.mockito.Mockito.*;

class TimebasedFragmentationServiceTest {

	private static final String VIEW_NAME = "view";

	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);

	private final FragmentCreator fragmentCreator = mock(FragmentCreator.class);

	private FragmentationService fragmentationService;

	private final FragmentationService wrappedService = mock(FragmentationService.class);

	private static LdesFragment ROOT_FRAGMENT;

	@BeforeEach
	void setUp() {
		ROOT_FRAGMENT = new LdesFragment("rootFragment",
				new FragmentInfo(VIEW_NAME, List.of()));
		fragmentationService = new TimebasedFragmentationService(wrappedService,
				fragmentCreator,
				ldesFragmentRepository, mockTracer());
	}

	@Test
	@DisplayName("Adding Member when there is no existing fragment")
	void when_NoFragmentExists_thenFragmentIsCreatedAndMemberIsAdded() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		LdesMember ldesMember = new LdesMember(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
		LdesFragment createdFragment = new LdesFragment("fragmentId",
				new FragmentInfo(VIEW_NAME, List.of(new FragmentPair("Path",
						"Value"))));
		when(ldesFragmentRepository.retrieveOpenChildFragment(VIEW_NAME,
				List.of()))
				.thenReturn(Optional.empty());
		when(fragmentCreator.createNewFragment(Optional.empty(), ROOT_FRAGMENT.getFragmentInfo()))
				.thenReturn(createdFragment);

		fragmentationService.addMemberToFragment(ROOT_FRAGMENT,
				ldesMember.getLdesMemberId());

		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentCreator);
		inOrder.verify(ldesFragmentRepository,
				times(1)).retrieveOpenChildFragment(VIEW_NAME,
						List.of());
		inOrder.verify(fragmentCreator, times(1)).createNewFragment(Optional.empty(),
				ROOT_FRAGMENT.getFragmentInfo());
		inOrder.verify(ldesFragmentRepository,
				times(1)).saveFragment(createdFragment);
		inOrder.verify(ldesFragmentRepository,
				times(1)).saveFragment(ROOT_FRAGMENT);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Adding Member when there is an incomplete fragment")
	void when_AnIncompleteFragmentExists_thenMemberIsAddedToFragment() throws IOException {
		String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		LdesMember ldesMember = new LdesMember(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
		LdesFragment existingLdesFragment = new LdesFragment("fragmentId",
				new FragmentInfo(VIEW_NAME, List.of(new FragmentPair("Path",
						"Value"))));
		when(ldesFragmentRepository.retrieveOpenChildFragment(VIEW_NAME,
				List.of()))
				.thenReturn(Optional.of(existingLdesFragment));

		fragmentationService.addMemberToFragment(ROOT_FRAGMENT,
				ldesMember.getLdesMemberId());

		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentCreator);
		inOrder.verify(ldesFragmentRepository,
				times(1)).retrieveOpenChildFragment(VIEW_NAME,
						List.of());
		inOrder.verify(ldesFragmentRepository,
				times(1)).saveFragment(existingLdesFragment);
		inOrder.verify(ldesFragmentRepository,
				times(1)).saveFragment(ROOT_FRAGMENT);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Adding Member when there is a complete fragment")
	void when_AFullFragmentExists_thenANewFragmentIsCreatedAndMemberIsAddedToNewFragment()
			throws IOException {
		String ldesMemberString = FileUtils.readFileToString(ResourceUtils.getFile("classpath:example-ldes-member.nq"),
				StandardCharsets.UTF_8);
		LdesMember ldesMember = new LdesMember(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				RdfModelConverter.fromString(ldesMemberString, Lang.NQUADS));
		LdesFragment existingLdesFragment = new LdesFragment("existingFragment",
				new FragmentInfo(VIEW_NAME, List.of(new FragmentPair("Path",
						"Value"))));
		LdesFragment newFragment = new LdesFragment("someId",
				new FragmentInfo(VIEW_NAME, List.of(new FragmentPair("Path",
						"Value"))));
		IntStream.range(0, 5).forEach(index -> existingLdesFragment.addMember("memberId"));

		when(ldesFragmentRepository.retrieveOpenChildFragment(VIEW_NAME,
				List.of()))
				.thenReturn(Optional.of(existingLdesFragment));
		when(fragmentCreator.needsToCreateNewFragment(existingLdesFragment)).thenReturn(true);
		when(fragmentCreator.createNewFragment(Optional.of(existingLdesFragment),
				ROOT_FRAGMENT.getFragmentInfo())).thenReturn(newFragment);

		fragmentationService.addMemberToFragment(ROOT_FRAGMENT,
				ldesMember.getLdesMemberId());

		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentCreator);
		inOrder.verify(ldesFragmentRepository,
				times(1)).retrieveOpenChildFragment(VIEW_NAME,
						List.of());
		inOrder.verify(fragmentCreator,
				times(1)).createNewFragment(Optional.of(existingLdesFragment), ROOT_FRAGMENT.getFragmentInfo());
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(newFragment);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(ROOT_FRAGMENT);
		inOrder.verifyNoMoreInteractions();
	}

}