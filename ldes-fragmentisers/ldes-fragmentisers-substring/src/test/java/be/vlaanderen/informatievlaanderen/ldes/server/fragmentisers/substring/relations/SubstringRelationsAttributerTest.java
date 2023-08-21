package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
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
	private static Fragment PARENT_FRAGMENT;
	private static Fragment CHILD_FRAGMENT;
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private FragmentRepository fragmentRepository;

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		CHILD_FRAGMENT = PARENT_FRAGMENT.createChild(new FragmentPair(SUBSTRING, "ab"));

		fragmentRepository = mock(FragmentRepository.class);
		SubstringConfig substringConfig = new SubstringConfig();
		substringConfig.setFragmenterPath("somefilter");
		substringRelationsAttributer = new SubstringRelationsAttributer(fragmentRepository,
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
		verify(fragmentRepository, times(1)).saveFragment(PARENT_FRAGMENT);
		verifyNoMoreInteractions(fragmentRepository);
	}
}
