package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class FragmentationStrategyDecoratorTest {
    FragmentationStrategy fragmentationStrategy = Mockito.mock(FragmentationStrategy.class);
    FragmentRepository fragmentRepository = mock(FragmentRepository.class);
    private FragmentationStrategyDecorator fragmentationStrategyDecorator;
    private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");

    @BeforeEach
    void setUp() {
        fragmentationStrategyDecorator = new FragmentationStrategyDecoratorTestImpl(fragmentationStrategy,
                fragmentRepository);
    }

    @Test
    void when_ParentDoesNotYetHaveRelationToChild_AddRelationAndSaveToDatabase() {

        Fragment parentFragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
        Fragment childFragment = parentFragment.createChild(new FragmentPair("key", "value"));
        TreeRelation expectedRelation = new TreeRelation("",
                childFragment.getFragmentId(), "", "",
                GENERIC_TREE_RELATION);

        fragmentationStrategyDecorator.addRelationFromParentToChild(parentFragment,
                childFragment);

        assertTrue(parentFragment.containsRelation(expectedRelation));
        verify(fragmentRepository).saveFragment(parentFragment);
    }

    @Test
    void when_DecoratorAddsMemberToFragment_WrappedFragmentationStrategyIsCalled() {
        Fragment parentFragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
        Member member = mock(Member.class);
        Observation span = mock(Observation.class);
        fragmentationStrategyDecorator.addMemberToBucket(parentFragment, member, span);
        fragmentationStrategyDecorator.saveBucket();
        verify(fragmentationStrategy).addMemberToBucket(parentFragment, member, span);
        verify(fragmentationStrategy).saveBucket();
    }

    static class FragmentationStrategyDecoratorTestImpl extends FragmentationStrategyDecorator {
        protected FragmentationStrategyDecoratorTestImpl(FragmentationStrategy fragmentationStrategy,
                                                         FragmentRepository fragmentRepository) {
            super(fragmentationStrategy, fragmentRepository);
        }
    }
}
