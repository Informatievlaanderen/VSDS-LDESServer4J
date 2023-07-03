package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class SubstringFragmentCreatorTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final FragmentPair timePair = new FragmentPair("time", "b");
	private static final FragmentPair substringPair = new FragmentPair("substring", "a");

	private LdesFragmentRepository ldesFragmentRepository;
	private SubstringFragmentCreator substringFragmentCreator;

	@BeforeEach
	void setUp() {
		ldesFragmentRepository = mock(LdesFragmentRepository.class);
		substringFragmentCreator = new SubstringFragmentCreator(ldesFragmentRepository);
	}

	@Test
	void when_FragmentDoesNotExist_NewSubstringFragmentIsCreated() {
		LdesFragment ldesFragment = new LdesFragment(
				new LdesFragmentIdentifier(VIEW_NAME, List.of(timePair)));
		LdesFragmentIdentifier exptectedFragmentId = ldesFragment.createChild(new FragmentPair("substring", "a"))
				.getFragmentId();
		when(ldesFragmentRepository.retrieveFragment(exptectedFragmentId)).thenReturn(Optional.empty());

		LdesFragment childFragment = substringFragmentCreator.getOrCreateSubstringFragment(ldesFragment,
				"a");

		assertEquals(new LdesFragmentIdentifier(VIEW_NAME, List.of(timePair, substringPair)),
				childFragment.getFragmentId());
		verify(ldesFragmentRepository,
				times(1)).retrieveFragment(exptectedFragmentId);
		verify(ldesFragmentRepository, times(1)).saveFragment(childFragment);
		verifyNoMoreInteractions(ldesFragmentRepository);
	}

	@Test
	void when_FragmentExists_RetrievedFragmentIsReturned() {
		LdesFragment ldesFragment = new LdesFragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(timePair)));
		LdesFragment substringFragment = ldesFragment.createChild(substringPair);

		when(ldesFragmentRepository.retrieveFragment(substringFragment.getFragmentId()))
				.thenReturn(Optional.of(substringFragment));

		LdesFragment childFragment = substringFragmentCreator.getOrCreateSubstringFragment(ldesFragment,
				"a");

		assertEquals(new LdesFragmentIdentifier(VIEW_NAME, List.of(timePair, substringPair)),
				childFragment.getFragmentId());
		verify(ldesFragmentRepository, times(1)).retrieveFragment(substringFragment.getFragmentId());
		verifyNoMoreInteractions(ldesFragmentRepository);
	}
}