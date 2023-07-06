package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class RefragmentationServiceImpl implements RefragmentationService {
	private final MemberRepository memberRepository;
	private final ObservationRegistry observationRegistry;

	public RefragmentationServiceImpl(MemberRepository memberRepository, ObservationRegistry observationRegistry) {
		this.memberRepository = memberRepository;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public void refragmentMembersForView(LdesFragment rootFragmentForView,
			FragmentationStrategy fragmentationStrategyForView) {
		Stream<Member> memberStreamOfCollection = memberRepository
				.getMemberStreamOfCollection(rootFragmentForView.getViewName().getCollectionName());
		memberStreamOfCollection
				.forEach(member -> fragmentMember(rootFragmentForView, fragmentationStrategyForView, member));
	}

	private void fragmentMember(LdesFragment rootFragmentForView, FragmentationStrategy fragmentationStrategyForView,
			Member member) {
		Observation parentObservation = Observation.createNotStarted("execute refragmentation",
				observationRegistry).start();
		fragmentationStrategyForView.addMemberToFragment(rootFragmentForView, member, parentObservation);
		parentObservation.stop();
	}
}
