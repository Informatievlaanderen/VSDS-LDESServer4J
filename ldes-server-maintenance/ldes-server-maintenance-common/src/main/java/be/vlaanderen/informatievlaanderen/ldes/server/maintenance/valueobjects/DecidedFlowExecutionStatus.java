package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.valueobjects;

import org.springframework.batch.core.job.flow.FlowExecutionStatus;

public enum DecidedFlowExecutionStatus {
	SKIP("SKIP"),
	CONTINUE("CONTINUE");

	private final String pattern;
	private final FlowExecutionStatus status;

	DecidedFlowExecutionStatus(String pattern) {
		this.pattern = pattern;
		this.status = new FlowExecutionStatus(pattern);
	}

	public String pattern() {
		return pattern;
	}

	public FlowExecutionStatus status() {
		return status;
	}
}
