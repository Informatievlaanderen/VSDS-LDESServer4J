package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator.SUBSTRING;
import static org.mockito.Mockito.*;

class SubstringRelationsAttributerTest {
	private SubstringRelationsAttributer substringRelationsAttributer;
	private NonCriticalTasksExecutor nonCriticalTasksExecutor;
	private static LdesFragment PARENT_FRAGMENT;
	private static LdesFragment CHILD_FRAGMENT;
	private static final String VIEW_NAME = "view";

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of()));
		CHILD_FRAGMENT = PARENT_FRAGMENT.createChild(new FragmentPair(SUBSTRING, "ab"));

		TreeRelationsRepository treeRelationsRepository = mock(TreeRelationsRepository.class);
		nonCriticalTasksExecutor = mock(NonCriticalTasksExecutor.class);
		SubstringConfig substringConfig = new SubstringConfig();
		substringConfig.setFragmenterPropertyQuery("somefilter");
		substringRelationsAttributer = new SubstringRelationsAttributer(treeRelationsRepository,
				nonCriticalTasksExecutor, substringConfig);
	}

	@Test
	void when_SubstringRelationIsAdded_TreeRelationsRepositoryAddsARelation() {
		substringRelationsAttributer.addSubstringRelation(PARENT_FRAGMENT,
				CHILD_FRAGMENT);

		verify(nonCriticalTasksExecutor, times(1)).submit(any());

	}
}