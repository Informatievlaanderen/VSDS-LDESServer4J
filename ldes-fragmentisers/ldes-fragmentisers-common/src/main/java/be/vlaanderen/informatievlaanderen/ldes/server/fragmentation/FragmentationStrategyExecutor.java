package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MembersToFragmentRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FragmentationStrategyExecutor {

	private final ExecutorService executorService;
	private final FragmentationStrategy fragmentationStrategy;
	private final ViewName viewName;
	private final RootFragmentRetriever rootFragmentRetriever;
	private final ObservationRegistry observationRegistry;
	private final MembersToFragmentRepository membersToFragmentRepository;

	public FragmentationStrategyExecutor(ViewName viewName, FragmentationStrategy fragmentationStrategy,
			RootFragmentRetriever rootFragmentRetriever,
			ObservationRegistry observationRegistry,
			MembersToFragmentRepository membersToFragmentRepository) {
		this.rootFragmentRetriever = rootFragmentRetriever;
		this.observationRegistry = observationRegistry;
		this.membersToFragmentRepository = membersToFragmentRepository;
		this.executorService = Executors.newSingleThreadExecutor();
		this.fragmentationStrategy = fragmentationStrategy;
		this.viewName = viewName;
	}

	// TODO TVB: 20/07/23 test
	public void execute() {
		executorService.execute(() -> {
			var parentObservation = Observation.createNotStarted("execute fragmentation", observationRegistry).start();
			membersToFragmentRepository.getNextMemberToFragment(viewName).ifPresent(member -> {
				Fragment rootFragmentOfView = rootFragmentRetriever.retrieveRootFragmentOfView(viewName,
						parentObservation);
				fragmentationStrategy
						.addMemberToFragment(rootFragmentOfView, member.id(), member.model(), parentObservation);
				membersToFragmentRepository.delete(viewName, member.sequenceNr());
			});
			parentObservation.stop();
		});
	}

	public boolean isPartOfCollection(String collectionName) {
		return Objects.equals(viewName.getCollectionName(), collectionName);
	}

	public ViewName getViewName() {
		return viewName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FragmentationStrategyExecutor that = (FragmentationStrategyExecutor) o;
		return Objects.equals(getViewName(), that.getViewName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getViewName());
	}
}
