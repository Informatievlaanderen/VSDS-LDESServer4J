package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import io.micrometer.core.instrument.MeterRegistry;
import org.awaitility.Durations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

class FragmentationQueueMediatorImplTest {

	private FragmentationQueueMediator fragmentationQueueMediator;

	private final FragmentationService fragmentationService = mock(FragmentationService.class);

	@MockBean(MeterRegistry.class)
	private MeterRegistry meterRegistry;

	@BeforeEach
	void setUp() {
		fragmentationQueueMediator = new FragmentationQueueMediatorImpl(meterRegistry, fragmentationService);
	}

	@Test
	@DisplayName("Adding a member to the queue")
	void when_MemberIsAddedForFragmentation_AThreadIsStartedWhichCallsTheFragmentationService() {
		fragmentationQueueMediator.addLdesMember("someMember");

		await()
				.pollDelay(Durations.ONE_MILLISECOND)
				.atMost(Durations.ONE_HUNDRED_MILLISECONDS)
				.until(fragmentationQueueMediator::queueIsEmtpy);

		verify(fragmentationService, times(1)).addMemberToFragment("someMember");
	}

}