package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.PROV_GENERATED_AT_TIME;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TimeBasedFragmentCreatorTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private TimeBasedFragmentCreator fragmentCreator;
	private FragmentRepository fragmentRepository;

	@BeforeEach
	void setUp() {
		fragmentRepository = mock(FragmentRepository.class);
		fragmentCreator = new TimeBasedFragmentCreator(
				fragmentRepository,
				createProperty(PROV_GENERATED_AT_TIME));
	}

	@Test
	@DisplayName("Creating First Time-Based Fragment")
	void when_NoFragmentExists_thenNewFragmentIsCreated() {
		Fragment parentFragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));

		Fragment newFragment = fragmentCreator.createNewFragment(parentFragment);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		verifyNoInteractions(fragmentRepository);
	}

	@Test
	@DisplayName("Creating New Time-Based Fragment")
	void when_AFragmentAlreadyExists_thenNewFragmentIsCreatedAndRelationsAreUpdated() {
		Fragment parentFragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));

		Fragment existingFragment = new Fragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(new FragmentPair(GENERATED_AT_TIME,
						"2020-12-28T09:36:37.127Z"))));

		Fragment newFragment = fragmentCreator.createNewFragment(existingFragment, parentFragment);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		InOrder inOrder = inOrder(fragmentRepository);
		inOrder.verify(fragmentRepository, times(1)).saveFragment(existingFragment);
		inOrder.verify(fragmentRepository, times(1)).saveFragment(newFragment);
		inOrder.verifyNoMoreInteractions();
	}

	private void verifyAssertionsOnAttributesOfFragment(Fragment fragment) {
		assertEquals("/collectionName/view?generatedAtTime",
				fragment.getFragmentIdString().split("=")[0]);
		assertEquals(VIEW_NAME, fragment.getViewName());
		assertTrue(fragment.getValueOfKey(GENERATED_AT_TIME).isPresent());
	}
}
