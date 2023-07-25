package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MemberToFragmentRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FragmentationService {

	private final FragmentationStrategyCollection fragmentationStrategyCollection;
	private final MemberToFragmentRepository memberToFragmentRepository;

	public FragmentationService(FragmentationStrategyCollection fragmentationStrategyCollection,
			MemberToFragmentRepository memberToFragmentRepository) {
		this.fragmentationStrategyCollection = fragmentationStrategyCollection;
		this.memberToFragmentRepository = memberToFragmentRepository;
	}

	@EventListener
	public void executeFragmentation(MemberIngestedEvent memberEvent) {
		final var collectionName = memberEvent.collectionName();
		final List<ViewName> views = fragmentationStrategyCollection.getViews(collectionName);
		final Member member = new Member(memberEvent.id(), memberEvent.model(), memberEvent.sequenceNr());
		memberToFragmentRepository.create(views, member);
		final var executors = fragmentationStrategyCollection.getFragmentationStrategyExecutors(collectionName);
		executors.forEach(FragmentationStrategyExecutor::executeNext);
	}

}
