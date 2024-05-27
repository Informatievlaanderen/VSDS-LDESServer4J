package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamClosedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MembersIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class FragmentationService {

	public static final String LDES_SERVER_CREATE_FRAGMENTS_COUNT = "ldes_server_create_fragments_count";

	private final FragmentationStrategyCollection fragmentationStrategyCollection;
	private final FragmentRepository fragmentRepository;

	public FragmentationService(FragmentationStrategyCollection fragmentationStrategyCollection,
								FragmentRepository fragmentRepository) {
		this.fragmentationStrategyCollection = fragmentationStrategyCollection;
        this.fragmentRepository = fragmentRepository;
    }

	@EventListener
	public void executeFragmentation(MembersIngestedEvent event) {
		fragmentationStrategyCollection
				.getFragmentationStrategyExecutors(event.collectionName())
				.forEach(FragmentationStrategyExecutor::execute);
	}

	@EventListener
	public void markFragmentsImmutableInCollection(EventStreamClosedEvent event) {
		fragmentRepository.markFragmentsImmutableInCollection(event.collectionName());
	}

}
