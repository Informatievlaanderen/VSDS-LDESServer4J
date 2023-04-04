package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class NonCriticalTasksExecutor {

	private final ExecutorService executors;

	public NonCriticalTasksExecutor(final ExecutorService executors) {
		this.executors = executors;
	}

	public void submit(Runnable task) {
		executors.submit(task);
	}
}
