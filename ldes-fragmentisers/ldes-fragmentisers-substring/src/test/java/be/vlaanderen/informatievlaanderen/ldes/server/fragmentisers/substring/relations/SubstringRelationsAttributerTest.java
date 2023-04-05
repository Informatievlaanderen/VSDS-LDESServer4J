package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator.SUBSTRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SubstringRelationsAttributerTest {
	private LdesFragmentRepository ldesFragmentRepository;
	private SubstringRelationsAttributer substringRelationsAttributer;

	private static LdesFragment PARENT_FRAGMENT;
	private static LdesFragment CHILD_FRAGMENT;
	private static final String VIEW_NAME = "view";
	private static final String FRAGMENTER_PROPERTY = "fragmenter#property";

	private static final TreeRelation EXPECTED_RELATION = new TreeRelation(FRAGMENTER_PROPERTY,
			"/view?substring=ab",
			"ab",
			"http://www.w3.org/2001/XMLSchema#string", "https://w3id.org/tree#SubstringRelation");

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of()));
		CHILD_FRAGMENT = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of(new FragmentPair(SUBSTRING, "ab"))));

		ldesFragmentRepository = mock(LdesFragmentRepository.class);
		SubstringConfig substringConfig = mock(SubstringConfig.class);
		when(substringConfig.getFragmenterProperty()).thenReturn(FRAGMENTER_PROPERTY);
		substringRelationsAttributer = new SubstringRelationsAttributer(ldesFragmentRepository, substringConfig);
	}

	@Test
	void when_ParentHasNotYetRelation_AddRelation() {
		substringRelationsAttributer.addSubstringRelation(PARENT_FRAGMENT, CHILD_FRAGMENT);

		assertTrue(CHILD_FRAGMENT.getRelations().isEmpty());
		assertEquals(List.of(EXPECTED_RELATION), PARENT_FRAGMENT.getRelations());
		verify(ldesFragmentRepository, times(1)).saveFragment(PARENT_FRAGMENT);
	}

	@Test
	void when_ParentHasAlreadyRelation_DoNotAddRelation() {
		PARENT_FRAGMENT.addRelation(EXPECTED_RELATION);

		substringRelationsAttributer.addSubstringRelation(PARENT_FRAGMENT, CHILD_FRAGMENT);

		assertTrue(CHILD_FRAGMENT.getRelations().isEmpty());
		assertEquals(List.of(EXPECTED_RELATION), PARENT_FRAGMENT.getRelations());
		verifyNoInteractions(ldesFragmentRepository);
	}
}