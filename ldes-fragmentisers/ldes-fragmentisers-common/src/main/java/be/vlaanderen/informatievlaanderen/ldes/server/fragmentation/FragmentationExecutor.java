package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FragmentationExecutor {

	private final Map<ViewName, Fragment> rootFragmentMap;
	private final FragmentRepository fragmentRepository;
	private final ObservationRegistry observationRegistry;
	private final FragmentationStrategyCollection fragmentationStrategyCollection;

	public FragmentationExecutor(
			FragmentRepository fragmentRepository, ObservationRegistry observationRegistry,
			FragmentationStrategyCollection fragmentationStrategyCollection) {
		this.fragmentationStrategyCollection = fragmentationStrategyCollection;
		this.rootFragmentMap = new ConcurrentHashMap<>();
		this.fragmentRepository = fragmentRepository;
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
					Fragment rootFragmentOfView = retrieveRootFragmentOfView(entry.getKey(), parentObservation);
					entry.getValue().addMemberToFragment(rootFragmentOfView, memberEvent.id(), memberEvent.model(),
							parentObservation);
				});

		parentObservation.stop();
	}

	private Fragment retrieveRootFragmentOfView(ViewName viewName, Observation parentObservation) {
		Observation rootRetrievalObservation = Observation
				.createNotStarted("retrieve root of view " + viewName, observationRegistry)
				.parentObservation(parentObservation).start();

		Fragment fragment = rootFragmentMap.computeIfAbsent(viewName, s -> fragmentRepository
				.retrieveRootFragment(viewName.asString())
				.orElseThrow(() -> new MissingRootFragmentException(viewName.asString())));

		rootRetrievalObservation.stop();
		return fragment;
	}
}
