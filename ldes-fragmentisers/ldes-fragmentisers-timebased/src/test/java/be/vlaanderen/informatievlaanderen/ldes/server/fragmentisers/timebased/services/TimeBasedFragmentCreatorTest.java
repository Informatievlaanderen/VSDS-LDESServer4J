package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;

class TimeBasedFragmentCreatorTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private TimeBasedFragmentCreator fragmentCreator;
	private LdesFragmentRepository ldesFragmentRepository;

	@BeforeEach
	void setUp() {
		ldesFragmentRepository = mock(LdesFragmentRepository.class);
		fragmentCreator = new TimeBasedFragmentCreator(
				ldesFragmentRepository,
				createProperty(PROV_GENERATED_AT_TIME));
	}

	@Test
	@DisplayName("Creating First Time-Based Fragment")
	void when_NoFragmentExists_thenNewFragmentIsCreated() {
		LdesFragment parentFragment = new LdesFragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));

		LdesFragment newFragment = fragmentCreator.createNewFragment(parentFragment);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		verifyNoInteractions(ldesFragmentRepository);
	}

	@Test
	@DisplayName("Creating New Time-Based Fragment")
	void when_AFragmentAlreadyExists_thenNewFragmentIsCreatedAndRelationsAreUpdated() {
		LdesFragment parentFragment = new LdesFragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));

		LdesFragment existingLdesFragment = new LdesFragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(new FragmentPair(GENERATED_AT_TIME,
						"2020-12-28T09:36:37.127Z"))));

		LdesFragment newFragment = fragmentCreator.createNewFragment(existingLdesFragment, parentFragment);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		InOrder inOrder = inOrder(ldesFragmentRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(existingLdesFragment);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(newFragment);
		inOrder.verifyNoMoreInteractions();
	}

	private void verifyAssertionsOnAttributesOfFragment(LdesFragment ldesFragment) {
		assertEquals("/collectionName/view?generatedAtTime",
				ldesFragment.getFragmentIdString().split("=")[0]);
		assertEquals(VIEW_NAME, ldesFragment.getViewName());
		assertTrue(ldesFragment.getValueOfKey(GENERATED_AT_TIME).isPresent());
	}
}
