package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
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

	private static final String VIEW = "view";
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
		LdesFragment parentFragment = new LdesFragment(VIEW,
				List.of());

		LdesFragment newFragment = fragmentCreator.createNewFragment(parentFragment);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		assertTrue(newFragment.getFragmentId().contains("/view?generatedAtTime="));
		verifyNoInteractions(ldesFragmentRepository);
	}

	@Test
	@DisplayName("Creating New Time-Based Fragment")
	void when_AFragmentAlreadyExists_thenNewFragmentIsCreatedAndRelationsAreUpdated() {
		LdesFragment parentFragment = new LdesFragment(VIEW,
				List.of());

		LdesFragment existingLdesFragment = new LdesFragment(
				VIEW, List.of(new FragmentPair(GENERATED_AT_TIME,
						"2020-12-28T09:36:37.127Z")));

		LdesFragment newFragment = fragmentCreator.createNewFragment(existingLdesFragment, parentFragment);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		assertTrue(newFragment.getFragmentId().contains("/view?generatedAtTime="));
		InOrder inOrder = inOrder(ldesFragmentRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(existingLdesFragment);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(newFragment);
		inOrder.verifyNoMoreInteractions();
	}

	private void verifyAssertionsOnAttributesOfFragment(LdesFragment ldesFragment) {
		assertEquals("/view?generatedAtTime",
				ldesFragment.getFragmentId().split("=")[0]);
		assertEquals(VIEW, ldesFragment.getViewName());
		assertTrue(ldesFragment.getValueOfKey(GENERATED_AT_TIME).isPresent());
	}
}
