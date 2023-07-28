package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.RootFragmentRetriever;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MemberToFragmentRepository;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class FragmentationStrategyExecutorCreatorImpl implements FragmentationStrategyExecutorCreator {

	private final FragmentRepository fragmentRepository;
	private final ObservationRegistry observationRegistry;
	private final MemberToFragmentRepository memberToFragmentRepository;

	public FragmentationStrategyExecutorCreatorImpl(FragmentRepository fragmentRepository,
			ObservationRegistry observationRegistry,
			MemberToFragmentRepository memberToFragmentRepository) {
		this.fragmentRepository = fragmentRepository;
		this.observationRegistry = observationRegistry;
		this.memberToFragmentRepository = memberToFragmentRepository;
	}

	public FragmentationStrategyExecutor createExecutor(ViewName viewName,
			FragmentationStrategy fragmentationStrategy) {
		final var rootFragmentRetriever = new RootFragmentRetriever(fragmentRepository, observationRegistry);
		return new FragmentationStrategyExecutor(viewName, fragmentationStrategy, rootFragmentRetriever,
				observationRegistry, memberToFragmentRepository, createExecutorService());
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
