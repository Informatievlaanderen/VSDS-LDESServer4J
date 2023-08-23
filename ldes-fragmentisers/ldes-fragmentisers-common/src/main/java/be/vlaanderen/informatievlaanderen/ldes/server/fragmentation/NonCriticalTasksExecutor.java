package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class NonCriticalTasksExecutor {

	private final ExecutorService executors;

	public NonCriticalTasksExecutor(ExecutorService executors) {
		this.executors = executors;
	}

	public NonCriticalTasksExecutor() {
		this(Executors.newCachedThreadPool());
	}

	public void submit(Runnable task) {
		executors.submit(task);
	}

}
