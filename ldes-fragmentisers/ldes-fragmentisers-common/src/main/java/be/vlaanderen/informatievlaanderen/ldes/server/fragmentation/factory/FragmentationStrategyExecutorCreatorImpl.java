package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.RootFragmentRetriever;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MemberToFragmentRepository;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;

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
				observationRegistry, memberToFragmentRepository, Executors.newSingleThreadExecutor());
	}

}
