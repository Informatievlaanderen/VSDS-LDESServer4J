package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.SubstringConstants.STRING_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.SubstringConstants.TREE_SUBSTRING_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator.SUBSTRING;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SubstringRelationsAttributerTest {
	private SubstringRelationsAttributer substringRelationsAttributer;
	private static LdesFragment PARENT_FRAGMENT;
	private static LdesFragment CHILD_FRAGMENT;
	private static final String VIEW_NAME = "view";
	private LdesFragmentRepository ldesFragmentRepository;

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new LdesFragment(
				VIEW_NAME, List.of());
		CHILD_FRAGMENT = PARENT_FRAGMENT.createChild(new FragmentPair(SUBSTRING, "ab"));

		ldesFragmentRepository = mock(LdesFragmentRepository.class);
		SubstringConfig substringConfig = new SubstringConfig();
		substringConfig.setFragmenterProperty("somefilter");
		substringRelationsAttributer = new SubstringRelationsAttributer(ldesFragmentRepository,
				substringConfig);
	}

	@Test
	void when_SubstringRelationIsAdded_TreeRelationsRepositoryAddsARelation() {
		substringRelationsAttributer.addSubstringRelation(PARENT_FRAGMENT,
				CHILD_FRAGMENT);

		assertTrue(PARENT_FRAGMENT.containsRelation(new TreeRelation("somefilter",
				CHILD_FRAGMENT.getFragmentId(),
				"ab", STRING_TYPE,
				TREE_SUBSTRING_RELATION)));
		verify(ldesFragmentRepository, times(1)).saveFragment(PARENT_FRAGMENT);
		verifyNoMoreInteractions(ldesFragmentRepository);
	}
}