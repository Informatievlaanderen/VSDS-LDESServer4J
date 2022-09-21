package be.vlaanderen.informatievlaanderen.ldes.server.domain;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TracerMockHelper {
	public static Tracer mockTracer() {
		Tracer tracer = mock(Tracer.class);
		Span span = mock(Span.class);
		when(tracer.nextSpan()).thenReturn(span);
		when(tracer.nextSpan(any())).thenReturn(span);
		when(span.name(anyString())).thenReturn(span);
		when(span.start()).thenReturn(span);

		return tracer;
	}
}
