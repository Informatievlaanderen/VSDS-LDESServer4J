package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RootFragmentRetrieverTest {

	@Mock
	private FragmentRepository fragmentRepository;

	private Observation observation;
	private RootFragmentRetriever rootFragmentRetriever;
	private Fragment rootFragment;

	private static final ObservationRegistry observationRegistry = ObservationRegistry.NOOP;
	private static final ViewName VIEW_NAME = new ViewName("collection", "view");

	@BeforeEach
	void setUp() {
		observation = Observation.createNotStarted("observation", observationRegistry).start();

		rootFragmentRetriever = new RootFragmentRetriever(fragmentRepository, observationRegistry);
		rootFragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		when(fragmentRepository.retrieveRootFragment(VIEW_NAME.asString())).thenReturn(Optional.of(rootFragment));
	}

	@Test
	void should_FetchRootFragment_when_NotYetInMemory() {
		final Fragment result = rootFragmentRetriever.retrieveRootFragmentOfView(VIEW_NAME, observation);

		assertEquals(rootFragment, result);
		verify(fragmentRepository).retrieveRootFragment(VIEW_NAME.asString());
	}

	@Test
	void should_ReturnFragmentFromMemory_when_FetchedEarlier() {
		rootFragmentRetriever.retrieveRootFragmentOfView(VIEW_NAME, observation);
		rootFragmentRetriever.retrieveRootFragmentOfView(VIEW_NAME, observation);
		rootFragmentRetriever.retrieveRootFragmentOfView(VIEW_NAME, observation);
		final Fragment result = rootFragmentRetriever.retrieveRootFragmentOfView(VIEW_NAME, observation);

		assertEquals(rootFragment, result);
		verify(fragmentRepository, times(1)).retrieveRootFragment(VIEW_NAME.asString());
	}

	@Test
    void should_ThrowException_when_RootFragmentIsNotFound() {
        when(fragmentRepository.retrieveRootFragment(VIEW_NAME.asString())).thenReturn(Optional.empty());

        assertThrows(MissingRootFragmentException.class,
                () -> rootFragmentRetriever.retrieveRootFragmentOfView(VIEW_NAME, observation));
    }
}
