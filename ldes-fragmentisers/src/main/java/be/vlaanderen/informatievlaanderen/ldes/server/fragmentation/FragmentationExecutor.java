package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FragmentationExecutor {

	private final Map<ViewName, LdesFragment> rootFragmentMap;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final ObservationRegistry observationRegistry;
	private final FragmentationStrategyCollection fragmentationStrategyCollection;

	public FragmentationExecutor(
			LdesFragmentRepository ldesFragmentRepository, ObservationRegistry observationRegistry,
			FragmentationStrategyCollection fragmentationStrategyCollection) {
		this.fragmentationStrategyCollection = fragmentationStrategyCollection;
		this.rootFragmentMap = new ConcurrentHashMap<>();
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.observationRegistry = observationRegistry;
	}

	@EventListener
	public synchronized void executeFragmentation(MemberIngestedEvent memberEvent) {
		Observation parentObservation = Observation.createNotStarted("execute fragmentation", observationRegistry)
				.start();

		fragmentationStrategyCollection
				.getFragmentationStrategyMap()
				.entrySet()
				.parallelStream()
				.filter(entry -> entry.getKey().getCollectionName().equals(memberEvent.collectionName()))
				.forEach(entry -> {
					LdesFragment rootFragmentOfView = retrieveRootFragmentOfView(entry.getKey(), parentObservation);
					entry.getValue().addMemberToFragment(rootFragmentOfView, memberEvent.id(), memberEvent.model(),
							parentObservation);
				});

		parentObservation.stop();
	}

	private LdesFragment retrieveRootFragmentOfView(ViewName viewName, Observation parentObservation) {
		Observation rootRetrievalObservation = Observation
				.createNotStarted("retrieve root of view " + viewName, observationRegistry)
				.parentObservation(parentObservation).start();

		LdesFragment ldesFragment = rootFragmentMap.computeIfAbsent(viewName, s -> ldesFragmentRepository
				.retrieveRootFragment(viewName.asString())
				.orElseThrow(() -> new MissingRootFragmentException(viewName.asString())));

		rootRetrievalObservation.stop();
		return ldesFragment;
	}
}
