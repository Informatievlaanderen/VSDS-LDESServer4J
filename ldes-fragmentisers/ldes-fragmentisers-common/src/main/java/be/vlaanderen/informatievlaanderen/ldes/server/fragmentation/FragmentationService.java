package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class FragmentationService {

	private final FragmentationStrategyCollection fragmentationStrategyCollection;

	public FragmentationService(FragmentationStrategyCollection fragmentationStrategyCollection) {
		this.fragmentationStrategyCollection = fragmentationStrategyCollection;
	}

	@EventListener
	public void executeFragmentation(MemberIngestedEvent memberEvent) {
		fragmentationStrategyCollection
				.getFragmentationStrategyExecutors(memberEvent.collectionName())
				.forEach(FragmentationStrategyExecutor::execute);
	}

}
