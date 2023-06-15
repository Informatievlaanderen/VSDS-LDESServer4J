package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongock;

import io.mongock.api.exception.MongockException;
import io.mongock.runner.core.executor.MongockRunner;

/**
 * Mongock runner that does nothing. Used when no migration packages are
 * provided.
 */
public class EmptyMongockRunner implements MongockRunner {
	@Override
	public boolean isExecutionInProgress() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public void forceEnable() {

	}

	@Override
	public void execute() throws MongockException {

	}
}
