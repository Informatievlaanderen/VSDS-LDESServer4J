package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class TimeBasedFragmentCreatorTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final FragmentPair timePair = new FragmentPair("Y", "2023");
	private static final Fragment PARENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of(timePair)));
	private static final FragmentationTimestamp TIME = new FragmentationTimestamp(LocalDateTime.of(2023, 1, 1, 0, 0, 0),
			Granularity.MONTH);

	private FragmentRepository fragmentRepository;
	private TimeBasedRelationsAttributer relationsAttributer;
	private TimeBasedFragmentCreator fragmentCreator;

	@BeforeEach
	void setUp() {
		fragmentRepository = mock(FragmentRepository.class);
		relationsAttributer = mock(TimeBasedRelationsAttributer.class);
		fragmentCreator = new TimeBasedFragmentCreator(fragmentRepository, relationsAttributer);
	}

	@Test
	void when_FragmentDoesNotExist_Then_NewFragmentIsCreated() {
		LdesFragmentIdentifier exptectedFragmentId = new LdesFragmentIdentifier(VIEW_NAME,
				List.of(timePair, new FragmentPair("M", "1")));
		when(fragmentRepository.retrieveFragment(exptectedFragmentId)).thenReturn(Optional.empty());

		Fragment child = fragmentCreator.getOrCreateFragment(PARENT, TIME, Granularity.MONTH);

		assertEquals(exptectedFragmentId, child.getFragmentId());
		verify(fragmentRepository,
				times(1)).retrieveFragment(exptectedFragmentId);
		verify(fragmentRepository, times(1)).saveFragment(child);
		verifyNoMoreInteractions(fragmentRepository);

	}

	@Test
	void when_FragmentDoesExist_Then_FragmentIsRetrieved() {
		Fragment expectedChild = PARENT.createChild(new FragmentPair("M", "1"));
		when(fragmentRepository.retrieveFragment(expectedChild.getFragmentId())).thenReturn(Optional.of(expectedChild));

		Fragment child = fragmentCreator.getOrCreateFragment(PARENT, TIME, Granularity.MONTH);

		assertEquals(expectedChild.getFragmentId(), child.getFragmentId());
		verify(fragmentRepository,
				times(1)).retrieveFragment(expectedChild.getFragmentId());
		verifyNoMoreInteractions(fragmentRepository);

	}

}