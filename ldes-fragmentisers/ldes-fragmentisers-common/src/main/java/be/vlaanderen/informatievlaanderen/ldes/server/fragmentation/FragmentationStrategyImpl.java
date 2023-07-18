package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.ApplicationEventPublisher;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	private final AllocationRepository allocationRepository;
	private final FragmentRepository fragmentRepository;
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor;
	private final ApplicationEventPublisher eventPublisher;

	public FragmentationStrategyImpl(FragmentRepository fragmentRepository,
			AllocationRepository allocationRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor, ApplicationEventPublisher eventPublisher) {
		this.allocationRepository = allocationRepository;
		this.nonCriticalTasksExecutor = nonCriticalTasksExecutor;
		this.fragmentRepository = fragmentRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void addMemberToFragment(Fragment fragment, String memberId, Model memberModel,
			Observation parentObservation) {
		nonCriticalTasksExecutor.submit(
				() -> {
					allocationRepository.allocateMemberToFragment(memberId, fragment.getViewName(),
							fragment.getFragmentIdString());
					eventPublisher.publishEvent(new MemberAllocatedEvent(memberId, fragment.getViewName()));
				});
		fragmentRepository.incrementNumberOfMembers(fragment.getFragmentId());

	}

}
