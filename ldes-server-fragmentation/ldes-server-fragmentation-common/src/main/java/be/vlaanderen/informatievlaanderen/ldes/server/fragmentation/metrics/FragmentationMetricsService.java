package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.FragmentationMetricsRepository;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FragmentationMetricsService {
	public static final String BUCKET = "ldes_server_bucket_members_count";
	public static final String PAGINATE = "ldes_server_pagination_members_count";
	public static final String COLLECTION = "collection";
	public static final String VIEW = "view";
	private final FragmentationMetricsRepository fragmentationMetricsRepository;
	private final Map<ViewName, AtomicInteger> membersBucketised = new HashMap<>();
	private final Map<ViewName, AtomicInteger> membersPaginated = new HashMap<>();

	public FragmentationMetricsService(FragmentationMetricsRepository fragmentationMetricsRepository) {
		this.fragmentationMetricsRepository = fragmentationMetricsRepository;
		Metrics.globalRegistry.config()
				.meterFilter(MeterFilter.denyNameStartsWith("spring.batch.item"))
				.meterFilter(MeterFilter.denyNameStartsWith("spring.batch.chunk"));
	}

	public synchronized void updateBucketCounts(String collection) {
		fragmentationMetricsRepository.getBucketisedMemberCounts(collection)
				.forEach(metric -> {
					var viewName = new ViewName(collection, metric.view());
					membersBucketised.computeIfAbsent(viewName, v ->
							Metrics.gauge(BUCKET, Tags.of(COLLECTION, v.getCollectionName(), VIEW,
											metric.view()),
									new AtomicInteger(metric.count())));
					membersBucketised.get(viewName).set(metric.count());
				});
	}

	public synchronized void resetBucketCount(ViewName viewName) {
		membersBucketised.computeIfAbsent(viewName, v ->
				Metrics.gauge(BUCKET, Tags.of(COLLECTION, v.getCollectionName(), VIEW,
								viewName.getViewName()),
						new AtomicInteger(0)));
		membersBucketised.get(viewName).set(0);
	}

	public synchronized void updatePaginationCounts(String collection) {
		fragmentationMetricsRepository.getPaginatedMemberCounts(collection)
				.forEach(metric -> {
					var viewName = new ViewName(collection, metric.view());
					membersPaginated.computeIfAbsent(viewName, v ->
							Metrics.gauge(PAGINATE, Tags.of(COLLECTION, v.getCollectionName(), VIEW,
											metric.view()),
									new AtomicInteger(metric.count())));
					membersPaginated.get(viewName).set(metric.count());
				});
	}

	public synchronized void resetPaginationCount(ViewName viewName) {
		membersPaginated.computeIfAbsent(viewName, v ->
				Metrics.gauge(PAGINATE, Tags.of(COLLECTION, v.getCollectionName(), VIEW,
								viewName.getViewName()),
						new AtomicInteger(0)));
		membersPaginated.get(viewName).set(0);
	}

	@EventListener
	public void handleEvenStreamCreated(EventStreamCreatedEvent event){
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
		updateBucketCounts(event.getViewName().getCollectionName());
		updatePaginationCounts(event.getViewName().getCollectionName());
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		resetBucketCount(event.getViewName());
		resetPaginationCount(event.getViewName());
	}
}
