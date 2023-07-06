package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import io.micrometer.observation.Observation;
import org.apache.jena.rdf.model.Model;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	private final MemberRepository memberRepository;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor;

	public FragmentationStrategyImpl(LdesFragmentRepository ldesFragmentRepository,
			MemberRepository memberRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor) {
		this.memberRepository = memberRepository;
		this.nonCriticalTasksExecutor = nonCriticalTasksExecutor;
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override public void addMemberToFragment(LdesFragment ldesFragment, String memberId, Model memberModel,
			Observation parentObservation) {
		nonCriticalTasksExecutor.submit(
				() -> memberRepository.addMemberReference(memberId,
						ldesFragment.getFragmentIdString()));
		ldesFragmentRepository.incrementNumberOfMembers(ldesFragment.getFragmentId());
	}

}
