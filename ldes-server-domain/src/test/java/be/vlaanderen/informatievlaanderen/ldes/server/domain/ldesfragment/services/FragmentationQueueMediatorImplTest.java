package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

class FragmentationQueueMediatorImplTest {

    private FragmentationQueueMediator fragmentationQueueMediator;

    private final FragmentationService fragmentationService = mock(FragmentationService.class);

    @BeforeEach
    void setUp() {
        fragmentationQueueMediator = new FragmentationQueueMediatorImpl(fragmentationService);
    }

    @Test
    @DisplayName("Adding a member to the queue")
    void when_MemberIsAddedForFragmentation_AThreadIsStartedWhichCallsTheFragmentationService() {
        fragmentationQueueMediator.addLdesMember("someMember");

        await()
                .pollDelay(Duration.ONE_MILLISECOND)
                .atMost(Duration.ONE_HUNDRED_MILLISECONDS)
                .until(fragmentationQueueMediator::queueIsEmtpy);
        verify(fragmentationService, times(1)).addMemberToFragment("someMember");
    }

}