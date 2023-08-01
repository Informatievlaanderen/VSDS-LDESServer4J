package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.RootFragmentRetriever;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.EventSourceService;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class FragmentationStrategyExecutorCreatorImpl implements FragmentationStrategyExecutorCreator {

	private final FragmentRepository fragmentRepository;
	private final ObservationRegistry observationRegistry;
	private final EventSourceService eventSourceService;
	private final FragmentSequenceRepository fragmentSequenceRepository;
	private final FragmentationStrategyCreator fragmentationStrategyCreator;

	public FragmentationStrategyExecutorCreatorImpl(FragmentRepository fragmentRepository,
			ObservationRegistry observationRegistry,
			EventSourceService eventSourceService,
			FragmentSequenceRepository fragmentSequenceRepository,
			FragmentationStrategyCreator fragmentationStrategyCreator) {
		this.fragmentRepository = fragmentRepository;
		this.observationRegistry = observationRegistry;
		this.eventSourceService = eventSourceService;

		this.fragmentSequenceRepository = fragmentSequenceRepository;
		this.fragmentationStrategyCreator = fragmentationStrategyCreator;
	}

	public FragmentationStrategyExecutor createExecutor(ViewName viewName,
			ViewSpecification viewSpecification) {
		final FragmentationStrategy fragmentationStrategy = fragmentationStrategyCreator
				.createFragmentationStrategyForView(viewSpecification);
		final var rootFragmentRetriever = new RootFragmentRetriever(fragmentRepository, observationRegistry);
		return new FragmentationStrategyExecutor(viewName, fragmentationStrategy, rootFragmentRetriever,
				observationRegistry, createExecutorService(), eventSourceService, fragmentSequenceRepository);
	}

	/**
	 * This ExecutorService has the following properties:
	 * - single threaded: For the time being we process members single threaded to
	 * ensure their order
	 * and avoid possible state issues of fragmentisers
	 * - blocking queue: Ingestion can go fast, we do not want the queue to fill up
	 * with tasks and consume too
	 * much memory. Tasks should be aware of this and using a loop with checks might
	 * be a good idea.
	 * - discard policy: When the queue is full, new tasks are discarded.
	 */
	private ExecutorService createExecutorService() {
		return new ThreadPoolExecutor(1, 1,
				0L, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<>(2, true), new ThreadPoolExecutor.DiscardPolicy());
	}

}
