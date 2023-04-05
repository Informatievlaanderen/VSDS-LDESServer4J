package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class TreeNodeRemoverImplTest {

	private final LdesFragmentRepository repository = mock(LdesFragmentRepository.class);
	private final Map<String, List<RetentionPolicy>> retentionPolicyMap = Map.of("view",
			List.of(new TimeBasedRetentionPolicy(0)));
	private final TreeNodeRemover treeNodeRemover = new TreeNodeRemoverImpl(repository, retentionPolicyMap);

	@Test
	void when_NodeIsImmutableAndSatisfiesRetentionPoliciesOfView_NodeCanBeSoftDeleted() {
		when(repository.retrieveImmutableFragmentsOfView("view"))
				.thenReturn(Stream.of(notReadyToDeleteFragment(), readyToDeleteFragment()));

		treeNodeRemover.removeTreeNodes();

		verify(repository, times(1)).retrieveImmutableFragmentsOfView("view");
	}

	private LdesFragment notReadyToDeleteFragment() {
		return new LdesFragment(new FragmentInfo("view", List.of(), true, LocalDateTime.now().plusDays(1)));
	}

	private LdesFragment readyToDeleteFragment() {
		return new LdesFragment(new FragmentInfo("view", List.of(), true, LocalDateTime.now()));
	}

}