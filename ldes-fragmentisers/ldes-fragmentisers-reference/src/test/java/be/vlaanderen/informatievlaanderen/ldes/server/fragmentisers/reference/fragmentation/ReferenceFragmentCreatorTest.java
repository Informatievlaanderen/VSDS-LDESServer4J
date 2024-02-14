package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations.ReferenceFragmentRelationsAttributer;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.ReferenceFragmentationStrategyWrapper.DEFAULT_FRAGMENTATION_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceFragmentCreator.FRAGMENT_KEY_REFERENCE_ROOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferenceFragmentCreatorTest {

    private static final ViewName viewName = new ViewName("collectionName", "view");
    private static final FragmentPair timebasedPair = new FragmentPair("year", "2023");
    private static final FragmentPair referenceRootPair = new FragmentPair(DEFAULT_FRAGMENTATION_KEY, FRAGMENT_KEY_REFERENCE_ROOT);
    private static final FragmentPair referencePair = new FragmentPair(DEFAULT_FRAGMENTATION_KEY, RDF.type.getURI());
    private static final FragmentPair defaultPair = new FragmentPair(DEFAULT_FRAGMENTATION_KEY, DEFAULT_BUCKET_STRING);

    private ReferenceFragmentCreator referenceFragmentCreator;

    @Mock
    private FragmentRepository fragmentRepository;

    @Mock
    private ReferenceFragmentRelationsAttributer relationsAttributer;

    @BeforeEach
    void setUp() {
        referenceFragmentCreator =
                new ReferenceFragmentCreator(fragmentRepository, relationsAttributer, DEFAULT_FRAGMENTATION_KEY);
    }

    @Test
    void when_ReferenceFragmentDoesNotExist_NewReferenceFragmentIsCreatedAndSaved() {
        Fragment fragment = new Fragment(new LdesFragmentIdentifier(viewName, List.of(timebasedPair)));
        Fragment rootFragment = fragment.createChild(referenceRootPair);
        LdesFragmentIdentifier tileFragmentId = fragment.createChild(referencePair).getFragmentId();

        when(fragmentRepository.retrieveFragment(tileFragmentId)).thenReturn(Optional.empty());

        Fragment childFragment = referenceFragmentCreator.getOrCreateFragment(fragment, RDF.type.getURI(), rootFragment);

        assertThat(new LdesFragmentIdentifier(viewName, List.of(timebasedPair, referencePair)))
                .isEqualTo(childFragment.getFragmentId());
        verify(fragmentRepository,
                times(1)).retrieveFragment(tileFragmentId);
        verify(fragmentRepository,
                times(1)).saveFragment(childFragment);
    }

    @Test
    void when_ReferenceFragmentDoesExist_RetrievedReferenceFragmentIsReturned() {
        Fragment fragment = new Fragment(new LdesFragmentIdentifier(viewName, List.of(timebasedPair)));
        Fragment rootFragment = fragment.createChild(referenceRootPair);
        Fragment tileFragment = fragment.createChild(referencePair);

        when(fragmentRepository.retrieveFragment(tileFragment.getFragmentId())).thenReturn(Optional.of(tileFragment));

        Fragment childFragment = referenceFragmentCreator.getOrCreateFragment(fragment, RDF.type.getURI(), rootFragment);

        assertThat(new LdesFragmentIdentifier(viewName, List.of(timebasedPair, referencePair)))
                .isEqualTo(childFragment.getFragmentId());
        verify(fragmentRepository,
                times(1)).retrieveFragment(tileFragment.getFragmentId());
        verifyNoMoreInteractions(fragmentRepository);
    }

    @Test
    void when_RootFragmentDoesNotExist_NewRootFragmentIsCreatedAndSaved() {
        Fragment fragment = new Fragment(new LdesFragmentIdentifier(
                viewName, List.of(timebasedPair)));
        Fragment rootFragment = fragment
                .createChild(referenceRootPair);

        when(fragmentRepository.retrieveFragment(rootFragment.getFragmentId())).thenReturn(Optional.empty());

        Fragment returnedFragment = referenceFragmentCreator.getOrCreateRootFragment(fragment,
                FRAGMENT_KEY_REFERENCE_ROOT);

        assertEquals(new LdesFragmentIdentifier(viewName, List.of(timebasedPair, referenceRootPair)),
                returnedFragment.getFragmentId());
        verify(fragmentRepository, times(1)).retrieveFragment(rootFragment.getFragmentId());
        verify(fragmentRepository, times(1)).saveFragment(returnedFragment);
    }

    @Test
    void when_RootFragmentDoesNotExist_RetrievedRootFragmentIsReturned() {
        Fragment fragment = new Fragment(new LdesFragmentIdentifier(viewName, List.of(timebasedPair)));
        Fragment rootFragment = fragment.createChild(referenceRootPair);
        when(fragmentRepository.retrieveFragment(rootFragment.getFragmentId())).thenReturn(Optional.of(rootFragment));

        Fragment returnedFragment =
                referenceFragmentCreator.getOrCreateFragment(fragment, FRAGMENT_KEY_REFERENCE_ROOT, rootFragment);

        assertThat(new LdesFragmentIdentifier(viewName, List.of(timebasedPair, referenceRootPair)))
                .isEqualTo(returnedFragment.getFragmentId());
        verify(fragmentRepository, times(1)).retrieveFragment(rootFragment.getFragmentId());
        verifyNoMoreInteractions(fragmentRepository);
    }
    @Test
    void when_DefaultFragmentDoesNotExist_DefaultFragmentIsCreatedAndSaved() {
        Fragment fragment = new Fragment(new LdesFragmentIdentifier(viewName, List.of(timebasedPair)));
        Fragment rootFragment = fragment.createChild(referenceRootPair);
        LdesFragmentIdentifier tileFragmentId = fragment.createChild(defaultPair).getFragmentId();

        when(fragmentRepository.retrieveFragment(tileFragmentId)).thenReturn(Optional.empty());

        Fragment childFragment = referenceFragmentCreator.getOrCreateFragment(fragment, DEFAULT_BUCKET_STRING, rootFragment);

        assertThat(new LdesFragmentIdentifier(viewName, List.of(timebasedPair, defaultPair)))
                .isEqualTo(childFragment.getFragmentId());
        verify(fragmentRepository,
                times(1)).retrieveFragment(tileFragmentId);
        verify(fragmentRepository,
                times(1)).saveFragment(childFragment);
    }

    @Test
    void when_DefaultFragmentDoesExist_RetrievedDefaultFragmentIsReturned() {
        Fragment fragment = new Fragment(new LdesFragmentIdentifier(viewName, List.of(timebasedPair)));
        Fragment rootFragment = fragment.createChild(referenceRootPair);
        Fragment tileFragment = fragment.createChild(defaultPair);

        when(fragmentRepository.retrieveFragment(tileFragment.getFragmentId())).thenReturn(Optional.of(tileFragment));

        Fragment childFragment = referenceFragmentCreator.getOrCreateFragment(fragment, DEFAULT_BUCKET_STRING, rootFragment);

        assertThat(new LdesFragmentIdentifier(viewName, List.of(timebasedPair, defaultPair)))
                .isEqualTo(childFragment.getFragmentId());
        verify(fragmentRepository,
                times(1)).retrieveFragment(tileFragment.getFragmentId());
        verifyNoMoreInteractions(fragmentRepository);
    }

}