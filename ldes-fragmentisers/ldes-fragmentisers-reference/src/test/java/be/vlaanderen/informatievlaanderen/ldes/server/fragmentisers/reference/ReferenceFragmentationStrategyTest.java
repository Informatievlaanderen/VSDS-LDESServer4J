package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.bucketising.ReferenceBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceFragmentCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.ReferenceFragmentationStrategyWrapper.DEFAULT_FRAGMENTATION_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceFragmentCreator.FRAGMENT_KEY_REFERENCE_ROOT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReferenceFragmentationStrategyTest {

    private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
    private static final Fragment PARENT_FRAGMENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
    private static final Fragment ROOT_TILE_FRAGMENT =
            PARENT_FRAGMENT.createChild(new FragmentPair(DEFAULT_FRAGMENTATION_KEY, FRAGMENT_KEY_REFERENCE_ROOT));

    private ReferenceBucketiser referenceBucketiser;
    private ReferenceFragmentCreator fragmentCreator;
    private final FragmentRepository treeRelationsRepository = mock(FragmentRepository.class);
    private FragmentationStrategy decoratedFragmentationStrategy;
    private ReferenceFragmentationStrategy referenceFragmentationStrategy;

    @BeforeEach
    void setUp() {
        referenceBucketiser = mock(ReferenceBucketiser.class);
        fragmentCreator = mock(ReferenceFragmentCreator.class);
        decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
        when(fragmentCreator.getOrCreateRootFragment(PARENT_FRAGMENT, FRAGMENT_KEY_REFERENCE_ROOT))
                .thenReturn(ROOT_TILE_FRAGMENT);
        referenceFragmentationStrategy = new ReferenceFragmentationStrategy(decoratedFragmentationStrategy,
                referenceBucketiser, fragmentCreator, ObservationRegistry.create(),
                treeRelationsRepository);
    }

    @Test
    void when_MemberIsAddedToFragment_ThenReferenceFragmentationIsApplied() {
        Member member = mock(Member.class);

        final var typePerceel = "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel";
        final var typeGebouw = "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Gebouw";
        final var typeAdres = "https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Adres";

        when(referenceBucketiser.bucketise(member.id(), member.model()))
                .thenReturn(Set.of(typePerceel, typeGebouw, typeAdres));
        Fragment referenceFragmentOne = mockCreationReferenceFragment(typePerceel);
        Fragment referenceFragmentTwo = mockCreationReferenceFragment(typeGebouw);
        Fragment referenceFragmentThree = mockCreationReferenceFragment(typeAdres);

        referenceFragmentationStrategy
                .addMemberToBucket(PARENT_FRAGMENT, member, mock(Observation.class));

        verify(decoratedFragmentationStrategy,
                times(1)).addMemberToBucket(eq(referenceFragmentOne),
                any(), any(Observation.class));
        verify(decoratedFragmentationStrategy,
                times(1)).addMemberToBucket(eq(referenceFragmentTwo),
                any(), any(Observation.class));
        verify(decoratedFragmentationStrategy,
                times(1)).addMemberToBucket(eq(referenceFragmentThree),
                any(), any(Observation.class));
        verifyNoMoreInteractions(decoratedFragmentationStrategy);
    }

    private Fragment mockCreationReferenceFragment(String tile) {
        Fragment referenceFragment = PARENT_FRAGMENT.createChild(new FragmentPair(DEFAULT_FRAGMENTATION_KEY, tile));
        when(fragmentCreator.getOrCreateFragment(PARENT_FRAGMENT, tile, ROOT_TILE_FRAGMENT))
                .thenReturn(referenceFragment);
        return referenceFragment;
    }

}