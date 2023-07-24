package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RootFragmentRetrieverTest {

    @Mock
    private FragmentRepository fragmentRepository;
    @Mock
    private ObservationRegistry observationRegistry;

    private static final ViewName VIEW_NAME = new ViewName("collection", "view");

    @Test
    void should_FetchRootFragment_when_NotYetInMemory() {
        final RootFragmentRetriever rootFragmentRetriever =
                new RootFragmentRetriever(fragmentRepository, observationRegistry);
        final Fragment rootFragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
        when(fragmentRepository.retrieveRootFragment(VIEW_NAME.asString()))
                .thenReturn(Optional.of(rootFragment));

        // TODO TVB: 24/07/23 fix

        final Fragment result = rootFragmentRetriever.retrieveRootFragmentOfView(VIEW_NAME, mock(Observation.class));

        assertEquals(rootFragment, result);
    }

    @Test
    void should_ReturnFragmentFromMemory_when_FetchedEarlier() {

    }

}