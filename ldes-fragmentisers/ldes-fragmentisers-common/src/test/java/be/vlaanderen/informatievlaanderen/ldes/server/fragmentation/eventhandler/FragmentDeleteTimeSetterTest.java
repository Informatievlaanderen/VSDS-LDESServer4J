package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.compaction.FragmentsCompactedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FragmentDeleteTimeSetterTest {
	@Spy
	private final ServerConfig serverConfig = new ServerConfig();
	@Mock
	private FragmentRepository fragmentRepository;
	@InjectMocks
	private FragmentDeleteTimeSetter fragmentDeleteTimeSetter;

	@BeforeEach
	void setUp() {
		serverConfig.setCompactionDuration("PT1M");
	}

	@Test
	void when_FragmentAreCompacted_TheirDeleteTimeIsSetAndSaved() {
		Fragment firstFragment = createFragment("mobility-hindrances/first");
		Fragment secondFragment = createFragment("mobility-hindrances/second");
		when(fragmentRepository.retrieveFragment(firstFragment.getFragmentId())).thenReturn(Optional.of(firstFragment));
		when(fragmentRepository.retrieveFragment(secondFragment.getFragmentId()))
				.thenReturn(Optional.of(secondFragment));

		fragmentDeleteTimeSetter.handleFragmentsCompactedEvent(
				new FragmentsCompactedEvent(firstFragment.getFragmentId(), secondFragment.getFragmentId()));

		assertNotNull(firstFragment.getDeleteTime());
		assertNotNull(secondFragment.getDeleteTime());
		verify(fragmentRepository).saveFragment(firstFragment);
		verify(fragmentRepository).saveFragment(secondFragment);
		verifyNoMoreInteractions(fragmentRepository);
	}

	private static Fragment createFragment(String viewName) {
		return new Fragment(new LdesFragmentIdentifier(viewName, List.of()));
	}
}