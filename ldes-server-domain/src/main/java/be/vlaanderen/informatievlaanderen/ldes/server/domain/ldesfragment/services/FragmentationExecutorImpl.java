package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FragmentationExecutorImpl implements FragmentationExecutor {

	private final Map<ViewName, FragmentationStrategy> fragmentationStrategyMap;
	private final Map<ViewName, LdesFragment> rootFragmentMap;
	private final LdesFragmentRepository ldesFragmentRepository;

	private final ObservationRegistry observationRegistry;

	public FragmentationExecutorImpl(
			@Qualifier("configured-fragmentation") Map<ViewName, FragmentationStrategy> fragmentationStrategyMap,
			LdesFragmentRepository ldesFragmentRepository, ObservationRegistry observationRegistry) {
		this.fragmentationStrategyMap = fragmentationStrategyMap;
		this.rootFragmentMap = new ConcurrentHashMap<>();
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public synchronized void executeFragmentation(Member member) {
		Observation parentObservation = Observation.createNotStarted("execute fragmentation",
				observationRegistry)
				.start();

		fragmentationStrategyMap
				.entrySet()
				.stream()
				.filter(entry -> entry.getKey().getCollectionName().equals(member.getCollectionName()))
				.toList()
				.parallelStream()
				.forEach(entry -> {
					LdesFragment rootFragmentOfView = retrieveRootFragmentOfView(entry.getKey(), parentObservation);
					entry.getValue().addMemberToFragment(rootFragmentOfView, member, parentObservation);
				});

		parentObservation.stop();
	}

	private LdesFragment retrieveRootFragmentOfView(ViewName viewName, Observation parentObservation) {
		Observation rootRetrievalObservation = Observation
				.createNotStarted("retrieve root of view " + viewName, observationRegistry)
				.parentObservation(parentObservation).start();

		LdesFragment ldesFragment = rootFragmentMap.computeIfAbsent(viewName, s -> ldesFragmentRepository
				.retrieveRootFragment(viewName.getFullName())
				.orElseThrow(() -> new MissingRootFragmentException(viewName.getFullName())));

		rootRetrievalObservation.stop();
		return ldesFragment;
	}
}
