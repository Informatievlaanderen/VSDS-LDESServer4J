package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SubstringFragmentCreatorTest {
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
				new FragmentInfo("view", List.of(new FragmentPair("time", "b"))));
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(ldesFragment.getFragmentInfo().getViewName(),
				List.of(new FragmentPair("time", "b"), new FragmentPair("substring", "a")));
		when(ldesFragmentRepository.retrieveFragment(ldesFragmentRequest)).thenReturn(Optional.empty());

		LdesFragment childFragment = substringFragmentCreator.getOrCreateSubstringFragment(ldesFragment,
				"a");

		assertEquals("/view?time=b&substring=a", childFragment.getFragmentId());
		verify(ldesFragmentRepository, times(1)).retrieveFragment(ldesFragmentRequest);
		verifyNoMoreInteractions(ldesFragmentRepository);
	}

	@Test
	void when_FragmentExists_RetrievedFragmentIsReturned() {
		LdesFragment ldesFragment = new LdesFragment(
				new FragmentInfo("view", List.of(new FragmentPair("time", "b"))));
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(ldesFragment.getFragmentInfo().getViewName(),
				List.of(new FragmentPair("time", "b"), new FragmentPair("substring", "a")));
		when(ldesFragmentRepository.retrieveFragment(ldesFragmentRequest))
				.thenReturn(Optional.of(ldesFragment.createChild(new FragmentPair("substring", "a"))));

		LdesFragment childFragment = substringFragmentCreator.getOrCreateSubstringFragment(ldesFragment,
				"a");

		assertEquals("/view?time=b&substring=a", childFragment.getFragmentId());
		verify(ldesFragmentRepository, times(1)).retrieveFragment(ldesFragmentRequest);
		verifyNoMoreInteractions(ldesFragmentRepository);
	}
}