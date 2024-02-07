package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TimeBasedFragmentCreatorTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final FragmentPair timePair = new FragmentPair(Granularity.YEAR.getValue(), "2023");
	private static final Fragment PARENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of(timePair)));
	private static final Fragment ROOT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
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
				List.of(timePair, new FragmentPair(Granularity.MONTH.getValue(), "01")));
		when(fragmentRepository.retrieveFragment(exptectedFragmentId)).thenReturn(Optional.empty());

		Fragment child = fragmentCreator.getOrCreateFragment(PARENT, TIME, Granularity.MONTH);

		assertEquals(exptectedFragmentId, child.getFragmentId());
		verify(fragmentRepository,
				times(1)).retrieveFragment(exptectedFragmentId);
		verify(relationsAttributer, times(1)).addInBetweenRelation(PARENT, child);
		verify(fragmentRepository, times(1)).saveFragment(child);
		verifyNoMoreInteractions(fragmentRepository);

	}

	@Test
	void when_FragmentDoesNotExistAndIsDefaultFragment_Then_NewFragmentIsCreated() {
		LdesFragmentIdentifier exptectedFragmentId = new LdesFragmentIdentifier(VIEW_NAME,
				List.of(new FragmentPair(Granularity.YEAR.getValue(), DEFAULT_BUCKET_STRING)));
		when(fragmentRepository.retrieveFragment(exptectedFragmentId)).thenReturn(Optional.empty());

		Fragment child = fragmentCreator.getOrCreateFragment(ROOT, DEFAULT_BUCKET_STRING, Granularity.YEAR);

		assertEquals(exptectedFragmentId, child.getFragmentId());
		verify(fragmentRepository,
				times(1)).retrieveFragment(exptectedFragmentId);
		verify(relationsAttributer, times(1)).addDefaultRelation(ROOT, child);
		verify(fragmentRepository, times(1)).saveFragment(child);
		verifyNoMoreInteractions(fragmentRepository);

	}

	@Test
	void when_FragmentDoesExist_Then_FragmentIsRetrieved() {
		Fragment expectedChild = PARENT.createChild(new FragmentPair(Granularity.MONTH.getValue(), "01"));
		when(fragmentRepository.retrieveFragment(expectedChild.getFragmentId())).thenReturn(Optional.of(expectedChild));

		Fragment child = fragmentCreator.getOrCreateFragment(PARENT, TIME, Granularity.MONTH);

		assertEquals(expectedChild.getFragmentId(), child.getFragmentId());
		verify(fragmentRepository,
				times(1)).retrieveFragment(expectedChild.getFragmentId());
		verifyNoInteractions(relationsAttributer);
		verifyNoMoreInteractions(fragmentRepository);

	}

}
