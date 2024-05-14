package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	private final FragmentRepository fragmentRepository;
	private final ApplicationEventPublisher eventPublisher;

	public FragmentationStrategyImpl(FragmentRepository fragmentRepository,
			ApplicationEventPublisher eventPublisher) {
		this.fragmentRepository = fragmentRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public List<BucketisedMember> addMemberToFragment(Fragment fragment, Member member,
													  Observation parentObservation) {
		return List.of(new BucketisedMember(member.id(), fragment.getViewName(), fragment.getFragmentIdString(), member.sequenceNr()));
	}
}
