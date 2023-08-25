package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.ApplicationEventPublisher;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	private final FragmentRepository fragmentRepository;
	private final ApplicationEventPublisher eventPublisher;

	public FragmentationStrategyImpl(FragmentRepository fragmentRepository,
			ApplicationEventPublisher eventPublisher) {
		this.fragmentRepository = fragmentRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void addMemberToFragment(Fragment fragment, String memberId, Model memberModel,
			Observation parentObservation) {
		eventPublisher.publishEvent(
				new MemberAllocatedEvent(memberId, fragment.getViewName().getCollectionName(),
						fragment.getViewName().getViewName(), fragment.getFragmentIdString()));
		fragmentRepository.incrementNumberOfMembers(fragment.getFragmentId());

	}

}
