package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.EventSourceService;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

import static io.micrometer.observation.Observation.createNotStarted;

public class FragmentationStrategyExecutor {

	private final ExecutorService executorService;
	private final FragmentationStrategy fragmentationStrategy;
	private final ViewName viewName;
	private final RootFragmentRetriever rootFragmentRetriever;
	private final ObservationRegistry observationRegistry;
	private final EventSourceService eventSourceService;
	private final FragmentSequenceRepository fragmentSequenceRepository;

	public FragmentationStrategyExecutor(ViewName viewName, FragmentationStrategy fragmentationStrategy,
										 RootFragmentRetriever rootFragmentRetriever,
										 ObservationRegistry observationRegistry,
										 ExecutorService executorService, EventSourceService eventSourceService,
										 FragmentSequenceRepository fragmentSequenceRepository) {
		this.rootFragmentRetriever = rootFragmentRetriever;
		this.observationRegistry = observationRegistry;
		this.eventSourceService = eventSourceService;
		this.executorService = executorService;
		this.fragmentationStrategy = fragmentationStrategy;
		this.viewName = viewName;
		this.fragmentSequenceRepository = fragmentSequenceRepository;
	}

	// TODO TVB: 28/07/23 test
	public void execute() {
		executorService.execute(addMembersToFragments());
	}

	private Runnable addMembersToFragments() {
		return () -> {
//			long sequenceNr = 5;
			FragmentSequence lastProcessedSequence = fragmentSequenceRepository.findLastProcessedSequence(viewName);
			// TODO TVB: 29/07/23 cleanup
			Optional<Member> nextMemberToFragment =
					eventSourceService.findFirstByCollectionNameAndSequenceNrGreaterThan(viewName.getCollectionName(), lastProcessedSequence.sequenceNr())
							.map(m -> new Member(m.getId(), m.getModel(), m.getSequenceNr()));
			while (nextMemberToFragment.isPresent()) {
				Member member = nextMemberToFragment.get();
				fragment(member);
//				sequenceNr++;
				lastProcessedSequence = new FragmentSequence(viewName, member.sequenceNr() + 1);
				fragmentSequenceRepository.saveLastProcessedSequence(lastProcessedSequence);
				nextMemberToFragment =
						eventSourceService.findFirstByCollectionNameAndSequenceNrGreaterThan(viewName.getCollectionName(), lastProcessedSequence.sequenceNr())
								.map(m -> new Member(m.getId(), m.getModel(), m.getSequenceNr()));
			}
		};
	}

	private void fragment(Member member) {
		var parentObservation = createNotStarted("execute fragmentation", observationRegistry).start();
		var rootFragmentOfView = rootFragmentRetriever.retrieveRootFragmentOfView(viewName, parentObservation);
		String memberId = member.id();
		Model memberModel = member.model();
		fragmentationStrategy.addMemberToFragment(rootFragmentOfView, memberId, memberModel, parentObservation);
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

}
