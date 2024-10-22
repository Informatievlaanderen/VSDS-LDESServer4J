package be.vlaanderen.informatievlaanderen.ldes.server.ingest.metrics;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.MemberMetricsRepository;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IngestionMetricsService {
	public static final String INGEST = "ldes_server_ingested_members_count";
	public static final String COLLECTION = "collection";
	private final MemberMetricsRepository memberMetricsRepository;
	private final Map<String, AtomicInteger> membersIngested = new HashMap<>();

	public IngestionMetricsService(MemberMetricsRepository memberMetricsRepository) {
		this.memberMetricsRepository = memberMetricsRepository;
		Metrics.globalRegistry.config()
				.meterFilter(MeterFilter.denyNameStartsWith("spring.batch.item"))
				.meterFilter(MeterFilter.denyNameStartsWith("spring.batch.chunk"));
	}

	public synchronized void incrementIngestCount(String collection, int count) {
		membersIngested.computeIfAbsent(collection, s ->
				Metrics.gauge(INGEST, Tags.of(COLLECTION, collection),
						new AtomicInteger(0)));
		membersIngested.get(collection).addAndGet(count);
	}

	public synchronized void resetIngestCount(String collection) {
		membersIngested.computeIfAbsent(collection, s ->
				Metrics.gauge(INGEST, Tags.of(COLLECTION, collection),
						new AtomicInteger(0)));
		membersIngested.get(collection).set(0);
	}

	public synchronized void updateIngestCount(String collection) {
		int count = memberMetricsRepository.getTotalCount(collection);
		membersIngested.computeIfAbsent(collection, s ->
				Metrics.gauge(INGEST, Tags.of(COLLECTION, collection),
						new AtomicInteger(count)));
		membersIngested.get(collection).set(count);
	}

	@EventListener
	public void handleEvenStreamCreated(EventStreamCreatedEvent event){
		resetIngestCount(event.eventStream().getCollection());
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		resetIngestCount(event.collectionName());
	}

	@EventListener
	public void handleViewCreatedEvent(ViewInitializationEvent event) {
		updateIngestCount(event.getViewName().getCollectionName());
	}
}
