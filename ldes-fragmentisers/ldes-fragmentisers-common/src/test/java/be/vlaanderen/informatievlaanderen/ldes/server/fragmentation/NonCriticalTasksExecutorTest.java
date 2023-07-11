package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;

class NonCriticalTasksExecutorTest {

	private final ExecutorService executorService = mock(ExecutorService.class);
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor = new NonCriticalTasksExecutor(executorService);

	@Test
	void when_nonCriticalTaskIsSubmitted_ItIsPassedToExecutorService() {
		Runnable runnable = mock(Runnable.class);
		nonCriticalTasksExecutor.submit(runnable);

		verify(executorService, times(1)).submit(runnable);
	}

}