package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MembersToFragmentRepository;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO TVB: 20/07/23 update tests
@Component
public class FragmentationExecutor {

	private final FragmentationStrategyCollection fragmentationStrategyCollection;
	private final MembersToFragmentRepository membersToFragmentRepository;

	// TODO TVB: 20/07/23 remove unused injections
	public FragmentationExecutor(
			FragmentRepository fragmentRepository, ObservationRegistry observationRegistry,
			FragmentationStrategyCollection fragmentationStrategyCollection,
			MembersToFragmentRepository membersToFragmentRepository) {
		this.fragmentationStrategyCollection = fragmentationStrategyCollection;
		this.membersToFragmentRepository = membersToFragmentRepository;
	}

	@EventListener
	public void executeFragmentation(MemberIngestedEvent memberEvent) {
		final List<ViewName> views = fragmentationStrategyCollection.getViews(memberEvent.collectionName());
		membersToFragmentRepository.create(views, memberEvent.model(), memberEvent.sequenceNr(), memberEvent.id());
		final var executors = fragmentationStrategyCollection.getFragmentationStrategyExecutors();
		executors.forEach(FragmentationStrategyExecutor::execute);
	}

}
