package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import org.apache.jena.rdf.model.Model;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	private final AllocationRepository allocationRepository;
	private final FragmentRepository fragmentRepository;
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor;

	public FragmentationStrategyImpl(FragmentRepository fragmentRepository,
			AllocationRepository allocationRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor) {
		this.allocationRepository = allocationRepository;
		this.nonCriticalTasksExecutor = nonCriticalTasksExecutor;
		this.fragmentRepository = fragmentRepository;
	}

	@Override
	public void addMemberToFragment(LdesFragment ldesFragment, String memberId, Model memberModel,
			Observation parentObservation) {
		nonCriticalTasksExecutor.submit(
				() -> allocationRepository.allocateMemberToFragment(memberId, ldesFragment.getViewName(),
						ldesFragment.getFragmentIdString()));
		fragmentRepository.incrementNumberOfMembers(ldesFragment.getFragmentId());
	}

}
