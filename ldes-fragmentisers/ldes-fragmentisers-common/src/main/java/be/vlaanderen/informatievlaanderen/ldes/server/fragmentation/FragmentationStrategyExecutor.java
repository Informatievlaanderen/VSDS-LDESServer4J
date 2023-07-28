package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MemberToFragmentRepository;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

import static io.micrometer.observation.Observation.createNotStarted;

public class FragmentationStrategyExecutor {

	private boolean readyForRequests = true;
	private final ExecutorService executorService;
	private final FragmentationStrategy fragmentationStrategy;
	private final ViewName viewName;
	private final RootFragmentRetriever rootFragmentRetriever;
	private final ObservationRegistry observationRegistry;
	private final MemberToFragmentRepository memberToFragmentRepository;

	public FragmentationStrategyExecutor(ViewName viewName, FragmentationStrategy fragmentationStrategy,
			RootFragmentRetriever rootFragmentRetriever,
			ObservationRegistry observationRegistry,
			MemberToFragmentRepository memberToFragmentRepository, ExecutorService executorService) {
		this.rootFragmentRetriever = rootFragmentRetriever;
		this.observationRegistry = observationRegistry;
		this.memberToFragmentRepository = memberToFragmentRepository;
		this.executorService = executorService;
		this.fragmentationStrategy = fragmentationStrategy;
		this.viewName = viewName;
	}

	// TODO TVB: 28/07/23 test
	public void execute() {
		if (readyForRequests) {
			executorService.execute(addMembersToFragments());
		}
	}

	private Runnable addMembersToFragments() {
		return () -> {
			Optional<Member> nextMemberToFragment = memberToFragmentRepository.getNextMemberToFragment(viewName);
			while (nextMemberToFragment.isPresent()) {
				fragment(nextMemberToFragment.get());
				nextMemberToFragment = memberToFragmentRepository.getNextMemberToFragment(viewName);
			}
		};
	}

	private void fragment(Member member) {
		var parentObservation = createNotStarted("execute fragmentation", observationRegistry).start();
		var rootFragmentOfView = rootFragmentRetriever.retrieveRootFragmentOfView(viewName, parentObservation);
		String memberId = member.id();
		Model memberModel = member.model();
		fragmentationStrategy.addMemberToFragment(rootFragmentOfView, memberId, memberModel, parentObservation);
		memberToFragmentRepository.delete(viewName, member.sequenceNr());
		parentObservation.stop();
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

	// TODO TVB: 27/07/23 test
	public void pause() {
		readyForRequests = false;
	}

	// TODO TVB: 28/07/23 test
	public void resume() {
		readyForRequests = true;
		execute();
	}
}
