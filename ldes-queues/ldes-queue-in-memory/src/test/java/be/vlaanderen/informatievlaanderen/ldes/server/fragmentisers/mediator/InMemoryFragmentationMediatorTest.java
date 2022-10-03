package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.mediator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

class InMemoryFragmentationMediatorTest {

	private InMemoryFragmentationMediator fragmentationMediator;

	private final FragmentationExecutor fragmentationExecutor = mock(FragmentationExecutor.class);

	@BeforeEach
	void setUp() {
		fragmentationMediator = new InMemoryFragmentationMediator(fragmentationExecutor);
	}

	@Test
	@DisplayName("Adding a member to the queue")
	void when_MemberIsAddedForFragmentation_AThreadIsStartedWhichCallsTheFragmentationExecutor() {
		Awaitility.reset();
		LdesMember ldesMember = mock(LdesMember.class);
		fragmentationMediator.addMemberToFragment(ldesMember);

		await().pollDelay(Durations.ONE_MILLISECOND)
				.atMost(Durations.ONE_HUNDRED_MILLISECONDS)
				.until(() -> fragmentationMediator.ldesMembersToFragment.isEmpty());

		verify(fragmentationExecutor, times(1)).executeFragmentation(ldesMember);
	}

}