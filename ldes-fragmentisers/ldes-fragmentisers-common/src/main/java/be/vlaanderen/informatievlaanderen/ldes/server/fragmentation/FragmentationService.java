package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MembersToFragmentRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FragmentationService {

	private final FragmentationStrategyCollection fragmentationStrategyCollection;
	private final MembersToFragmentRepository membersToFragmentRepository;

	public FragmentationService(FragmentationStrategyCollection fragmentationStrategyCollection,
			MembersToFragmentRepository membersToFragmentRepository) {
		this.fragmentationStrategyCollection = fragmentationStrategyCollection;
		this.membersToFragmentRepository = membersToFragmentRepository;
	}

	@EventListener
	public void executeFragmentation(MemberIngestedEvent memberEvent) {
		final var collectionName = memberEvent.collectionName();
		final List<ViewName> views = fragmentationStrategyCollection.getViews(collectionName);
		membersToFragmentRepository.create(views, memberEvent.model(), memberEvent.sequenceNr(), memberEvent.id());
		final var executors = fragmentationStrategyCollection.getFragmentationStrategyExecutors(collectionName);
		executors.forEach(FragmentationStrategyExecutor::executeNext);
	}

}
