package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.decider;

import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.services.RetentionPolicyEmptinessChecker;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.valueobjects.DecidedFlowExecutionStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RetentionExecutionDeciderTest {
	private static final FlowExecutionStatus SKIP_STATUS = DecidedFlowExecutionStatus.SKIP.status();
	private static final FlowExecutionStatus CONTINUE_STATUS = DecidedFlowExecutionStatus.CONTINUE.status();
	private static final RetentionPolicyEmptinessChecker emptyEmptinessChecker = () -> true;
	private static final RetentionPolicyEmptinessChecker nonEmptyEmptinessChecker = () -> false;

	@ParameterizedTest
	@MethodSource
	void testDecide(List<RetentionPolicyEmptinessChecker> emptinessCheckers, FlowExecutionStatus expectedStatus) {
		final JobExecutionDecider retentionExecutionDecider = new RetentionExecutionDecider(emptinessCheckers);

		final FlowExecutionStatus result = retentionExecutionDecider.decide(null, null);

		assertThat(result).isEqualTo(expectedStatus);
	}

	static Stream<Arguments> testDecide() {
		return Stream.of(
				Arguments.of(List.of(emptyEmptinessChecker), SKIP_STATUS),
				Arguments.of(List.of(nonEmptyEmptinessChecker), CONTINUE_STATUS),
				Arguments.of(List.of(emptyEmptinessChecker, nonEmptyEmptinessChecker), CONTINUE_STATUS),
				Arguments.of(List.of(nonEmptyEmptinessChecker, nonEmptyEmptinessChecker), CONTINUE_STATUS),
				Arguments.of(List.of(emptyEmptinessChecker, emptyEmptinessChecker), SKIP_STATUS)
		);
	}
}