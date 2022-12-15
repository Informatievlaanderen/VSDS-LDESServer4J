package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.memberreferences.entities.MemberReferencesRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	private final LdesFragmentRepository ldesFragmentRepository;
	private final MemberReferencesRepository memberReferencesRepository;
	private final ObservationRegistry observationRegistry;

	public FragmentationStrategyImpl(LdesFragmentRepository ldesFragmentRepository,
			MemberReferencesRepository memberReferencesRepository, ObservationRegistry observationRegistry) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.memberReferencesRepository = memberReferencesRepository;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public void addMemberToFragment(LdesFragment ldesFragment, Member member, Observation parentObservation) {
		Observation finalSpan = Observation.start("add member to fragment", observationRegistry);
		ldesFragment.addMember(member.getLdesMemberId());
		ldesFragmentRepository.saveFragment(ldesFragment);
		memberReferencesRepository.saveMemberReference(member.getLdesMemberId(), ldesFragment.getFragmentId());
		finalSpan.stop();
	}

}
