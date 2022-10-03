package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	private final LdesFragmentRepository ldesFragmentRepository;
	private final Tracer tracer;

	public FragmentationStrategyImpl(LdesFragmentRepository ldesFragmentRepository,
			Tracer tracer) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.tracer = tracer;
	}

	@Override
	public void addMemberToFragment(LdesFragment ldesFragment, LdesMember ldesMember, Span parentSpan) {
		Span finalSpan = tracer.nextSpan(parentSpan).name("add member to fragment").start();
		ldesFragment.addMember(ldesMember.getLdesMemberId());
		ldesFragmentRepository.saveFragment(ldesFragment);
		finalSpan.end();
	}

}
