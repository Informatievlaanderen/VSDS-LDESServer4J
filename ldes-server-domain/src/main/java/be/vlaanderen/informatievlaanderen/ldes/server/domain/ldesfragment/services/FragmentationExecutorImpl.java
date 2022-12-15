package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FragmentationExecutorImpl implements FragmentationExecutor {

	private final Map<String, FragmentationStrategy> fragmentationStrategyMap;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final ObservationRegistry observationRegistry;

	public FragmentationExecutorImpl(Map<String, FragmentationStrategy> fragmentationStrategyMap,
			LdesFragmentRepository ldesFragmentRepository, ObservationRegistry observationRegistry) {
		this.fragmentationStrategyMap = fragmentationStrategyMap;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public synchronized void executeFragmentation(Member member) {
		Observation observation = Observation.start("execute fragmentation", observationRegistry);
		fragmentationStrategyMap.entrySet().parallelStream().forEach(entry -> {
			LdesFragment rootFragmentOfView = retrieveRootFragmentOfView(entry.getKey(), observation);
			entry.getValue().addMemberToFragment(rootFragmentOfView, member, observation);
		});
		observation.stop();
	}

	private LdesFragment retrieveRootFragmentOfView(String viewName, Observation parentObservation) {
		Observation observation = Observation.createNotStarted("retrieve root of view " + viewName, observationRegistry)
				.parentObservation(parentObservation).start();
		LdesFragment ldesFragment = ldesFragmentRepository
				.retrieveRootFragment(viewName)
				.orElseThrow(() -> new MissingRootFragmentException(viewName));
		observation.stop();
		return ldesFragment;
	}
}
