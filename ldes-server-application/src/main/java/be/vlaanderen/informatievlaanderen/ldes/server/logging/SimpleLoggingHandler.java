package be.vlaanderen.informatievlaanderen.ldes.server.logging;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.aop.ObservedAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SimpleLoggingHandler implements ObservationHandler<ObservedAspect.ObservedAspectContext> {
	private static final Logger log = LoggerFactory.getLogger(SimpleLoggingHandler.class);

	@Override
	public boolean supportsContext(@NotNull Observation.Context context) {
		return context instanceof ObservedAspect.ObservedAspectContext;
	}

	@Override
	public void onError(@NotNull ObservedAspect.ObservedAspectContext context) {
		log.atError().log(() -> getErrorInfo(context));
	}

	private String getErrorInfo(@NotNull ObservedAspect.ObservedAspectContext context) {
		ProceedingJoinPoint joinPoint = context.getProceedingJoinPoint();
		String problem = Objects.requireNonNull(context.getError()).getMessage();
		String source = joinPoint.getSignature().getDeclaringType().getSimpleName();
		String when = joinPoint.getSignature().getName();
		return "ERROR - source='%s', when='%s', problem='%s'".formatted(source, when, problem);
	}
}
