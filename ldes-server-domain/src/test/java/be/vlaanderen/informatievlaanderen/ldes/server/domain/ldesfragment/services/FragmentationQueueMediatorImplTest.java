package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.awaitility.Durations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.List;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

class FragmentationQueueMediatorImplTest {

	private FragmentationQueueMediator fragmentationQueueMediator;

	private final ListableBeanFactory listableBeanFactory = mock(ListableBeanFactory.class);

	private final FragmentationService fragmentationService = mock(FragmentationService.class);

	@BeforeEach
	void setUp() {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setFragmentations(Map.of("example", new Object()));
		when(listableBeanFactory.getBeansOfType(FragmentationService.class))
				.thenReturn(Map.of("example", fragmentationService));
		fragmentationQueueMediator = new FragmentationQueueMediatorImpl(listableBeanFactory, ldesConfig,
				new SimpleMeterRegistry());
	}

	@Test
	@DisplayName("Adding a member to the queue")
	void when_MemberIsAddedForFragmentation_AThreadIsStartedWhichCallsTheFragmentationService() {
		fragmentationQueueMediator.addLdesMember("someMember");

		await()
				.pollDelay(Durations.ONE_MILLISECOND)
				.atMost(Durations.ONE_HUNDRED_MILLISECONDS)
				.until(fragmentationQueueMediator::queueIsEmtpy);

		verify(fragmentationService, times(1)).addMemberToFragment(List.of(), "someMember");
	}

}