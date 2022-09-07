package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.List;

public class FragmentationServiceImpl implements FragmentationService {
	private final LdesFragmentRepository ldesFragmentRepository;
	private final LdesMemberRepository ldesMemberRepository;
	private final LdesConfig ldesConfig;
	private final Tracer tracer;

	public FragmentationServiceImpl(LdesFragmentRepository ldesFragmentRepository,
			LdesMemberRepository ldesMemberRepository, LdesConfig ldesConfig, Tracer tracer) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.ldesMemberRepository = ldesMemberRepository;
		this.ldesConfig = ldesConfig;
		this.tracer = tracer;
		addRootFragment();
	}

	private void addRootFragment() {
		FragmentInfo fragmentInfo = new FragmentInfo(
				ldesConfig.getCollectionName(),
				List.of());
		LdesFragment ldesFragment = new LdesFragment(
				LdesFragmentNamingStrategy.generateFragmentName(ldesConfig, fragmentInfo),
				fragmentInfo);
		ldesFragmentRepository.saveFragment(ldesFragment);
	}

	@Override
	public void addMemberToFragment(LdesFragment ldesFragment, String ldesMemberId) {
		Span newSpan = this.tracer.nextSpan().name("Member fragmentation").start();
		LdesMember ldesMember = ldesMemberRepository.getLdesMemberById(ldesMemberId)
				.orElseThrow(() -> new MemberNotFoundException(ldesMemberId));
		ldesFragment.addMember(ldesMember.getLdesMemberId());
		ldesFragmentRepository.saveFragment(ldesFragment);
		newSpan.end();
	}

}
