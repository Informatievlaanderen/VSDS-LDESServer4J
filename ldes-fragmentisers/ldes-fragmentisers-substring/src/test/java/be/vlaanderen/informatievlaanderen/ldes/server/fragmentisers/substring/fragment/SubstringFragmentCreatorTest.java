package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SubstringFragmentCreatorTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final FragmentPair timePair = new FragmentPair("time", "b");
	private static final FragmentPair substringPair = new FragmentPair("substring", "a");

	private FragmentRepository fragmentRepository;
	private SubstringFragmentCreator substringFragmentCreator;

	@BeforeEach
	void setUp() {
		fragmentRepository = mock(FragmentRepository.class);
		substringFragmentCreator = new SubstringFragmentCreator(fragmentRepository);
	}

	@Test
	void when_FragmentDoesNotExist_NewSubstringFragmentIsCreated() {
		Fragment fragment = new Fragment(
				new LdesFragmentIdentifier(VIEW_NAME, List.of(timePair)));
		LdesFragmentIdentifier exptectedFragmentId = fragment.createChild(new FragmentPair("substring", "a"))
				.getFragmentId();
		when(fragmentRepository.retrieveFragment(exptectedFragmentId)).thenReturn(Optional.empty());

		Fragment childFragment = substringFragmentCreator.getOrCreateSubstringFragment(fragment,
				"a");

		assertEquals(new LdesFragmentIdentifier(VIEW_NAME, List.of(timePair, substringPair)),
				childFragment.getFragmentId());
		verify(fragmentRepository,
				times(1)).retrieveFragment(exptectedFragmentId);
		verify(fragmentRepository, times(1)).saveFragment(childFragment);
		verifyNoMoreInteractions(fragmentRepository);
	}

	@Test
	void when_FragmentExists_RetrievedFragmentIsReturned() {
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(timePair)));
		Fragment substringFragment = fragment.createChild(substringPair);

		when(fragmentRepository.retrieveFragment(substringFragment.getFragmentId()))
				.thenReturn(Optional.of(substringFragment));

		Fragment childFragment = substringFragmentCreator.getOrCreateSubstringFragment(fragment,
				"a");

		assertEquals(new LdesFragmentIdentifier(VIEW_NAME, List.of(timePair, substringPair)),
				childFragment.getFragmentId());
		verify(fragmentRepository, times(1)).retrieveFragment(substringFragment.getFragmentId());
		verifyNoMoreInteractions(fragmentRepository);
	}
}