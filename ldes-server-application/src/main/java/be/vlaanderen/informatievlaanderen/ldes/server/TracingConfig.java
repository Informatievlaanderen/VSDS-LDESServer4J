package be.vlaanderen.informatievlaanderen.ldes.server;

import io.micrometer.tracing.otel.bridge.CompositeSpanExporter;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Configuration
public class TracingConfig implements BeanPostProcessor {
	@Override
	public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
		if(bean instanceof ZipkinSpanExporter zipkinSpanExporter) {
			return new FilteredZipkinExporter(zipkinSpanExporter);
		}
		return bean;
	}

	static class FilteredZipkinExporter extends CompositeSpanExporter {

		public FilteredZipkinExporter(ZipkinSpanExporter zipkinSpanExporter) {
			super(List.of(zipkinSpanExporter), null, null, null);
		}

		@Override
		public CompletableResultCode export(Collection<SpanData> spans) {
			Set<String> actuatorTraceIds = spans.stream()
					.filter(span -> span.getName().contains("actuator"))
					.map(SpanData::getTraceId)
					.collect(toSet());

			List<SpanData> nonActuatorSpans = spans.stream()
					.filter(span -> !actuatorTraceIds.contains(span.getTraceId()))
					.collect(toList());

			return super.export(nonActuatorSpans);
		}
	}

}
