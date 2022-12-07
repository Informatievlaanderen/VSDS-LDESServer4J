package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class NonCriticalTasksExecutor {

	private final ExecutorService executors;

	public NonCriticalTasksExecutor() {
		this.executors = Executors.newCachedThreadPool();
	}

	public void submit(Runnable task) {
		executors.submit(task);
	}
}
