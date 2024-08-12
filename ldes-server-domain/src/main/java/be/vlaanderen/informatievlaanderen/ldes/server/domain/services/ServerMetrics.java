package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ServerMetrics {
	public static final String INGEST = "ldes_server_ingested_members_count";
	public static final String BUCKET = "ldes_server_bucket_members_count";
	public static final String PAGINATE = "ldes_server_pagination_members_count";
	private final FragmentationMetricsRepository fragmentationMetricsRepository;
	private final MemberMetricsRepository memberMetricsRepository;
	private final Map<String, AtomicInteger> membersIngested = new HashMap<>();
	private final Map<ViewName, AtomicInteger> membersBucketised = new HashMap<>();
	private final Map<ViewName, AtomicInteger> membersPaginated = new HashMap<>();

	public ServerMetrics(FragmentationMetricsRepository fragmentationMetricsRepository, MemberMetricsRepository memberMetricsRepository) {
		this.fragmentationMetricsRepository = fragmentationMetricsRepository;
		this.memberMetricsRepository = memberMetricsRepository;
	}

	public synchronized void incrementIngestCount(String collection, int count) {
		membersIngested.computeIfAbsent(collection, s ->
				Metrics.gauge(INGEST, Tags.of("collection", collection),
						new AtomicInteger(0)));
		membersIngested.get(collection).addAndGet(count);
	}

	public synchronized void resetIngestCount(String collection) {
		membersIngested.computeIfAbsent(collection, s ->
				Metrics.gauge(INGEST, Tags.of("collection", collection),
						new AtomicInteger(0)));
		membersIngested.get(collection).set(0);
	}

	public synchronized void updateIngestCount(String collection) {
		int count = memberMetricsRepository.getTotalCount(collection);
		membersIngested.computeIfAbsent(collection, s ->
				Metrics.gauge(INGEST, Tags.of("collection", collection),
						new AtomicInteger(count)));
		membersIngested.get(collection).set(count);
	}

	public synchronized void updateBucketCounts(String collection) {
		fragmentationMetricsRepository.getBucketisedMemberCounts(collection)
				.forEach(metric -> {
					var viewName = new ViewName(collection, metric.view());
					membersBucketised.computeIfAbsent(viewName, v ->
							Metrics.gauge(BUCKET, Tags.of("collection", v.getCollectionName(), "view",
											metric.view()),
									new AtomicInteger(metric.count())));
					membersBucketised.get(viewName).set(metric.count());
				});
	}

	public synchronized void resetBucketCount(ViewName viewName) {
		membersBucketised.computeIfAbsent(viewName, v ->
				Metrics.gauge(BUCKET, Tags.of("collection", v.getCollectionName(), "view",
								viewName.getViewName()),
						new AtomicInteger(0)));
		membersBucketised.get(viewName).set(0);
	}

	public synchronized void updatePaginationCounts(String collection) {
		fragmentationMetricsRepository.getPaginatedMemberCounts(collection)
				.forEach(metric -> {
					var viewName = new ViewName(collection, metric.view());
					membersPaginated.computeIfAbsent(viewName, v ->
							Metrics.gauge(PAGINATE, Tags.of("collection", v.getCollectionName(), "view",
											metric.view()),
									new AtomicInteger(metric.count())));
					membersPaginated.get(viewName).set(metric.count());
				});
	}

	public synchronized void resetPaginationCount(ViewName viewName) {
		membersPaginated.computeIfAbsent(viewName, v ->
				Metrics.gauge(PAGINATE, Tags.of("collection", v.getCollectionName(), "view",
								viewName.getViewName()),
						new AtomicInteger(0)));
		membersPaginated.get(viewName).set(0);
	}

	@EventListener
	public void handleEvenStreamCreated(EventStreamCreatedEvent event){
		resetIngestCount(event.eventStream().getCollection());

		membersBucketised.keySet()
				.stream()
				.filter(viewName -> viewName.getCollectionName().equals(event.eventStream().getCollection()))
				.forEach(this::resetBucketCount);
		membersPaginated.keySet()
				.stream()
				.filter(viewName -> viewName.getCollectionName().equals(event.eventStream().getCollection()))
				.forEach(this::resetPaginationCount);
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		resetIngestCount(event.collectionName());

		membersBucketised.keySet()
				.stream()
				.filter(viewName -> viewName.getCollectionName().equals(event.collectionName()))
				.forEach(this::resetBucketCount);
		membersPaginated.keySet()
				.stream()
				.filter(viewName -> viewName.getCollectionName().equals(event.collectionName()))
				.forEach(this::resetPaginationCount);
	}

	@EventListener
	public void handleViewCreatedEvent(ViewInitializationEvent event) {
		updateIngestCount(event.getViewName().getCollectionName());
		updateBucketCounts(event.getViewName().getCollectionName());
		updatePaginationCounts(event.getViewName().getCollectionName());
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		resetBucketCount(event.getViewName());
		resetPaginationCount(event.getViewName());
	}
}
