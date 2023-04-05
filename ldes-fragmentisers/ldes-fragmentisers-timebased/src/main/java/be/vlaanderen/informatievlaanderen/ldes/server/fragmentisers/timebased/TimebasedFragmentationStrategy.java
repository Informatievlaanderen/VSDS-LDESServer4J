package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.OpenFragmentProvider;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

public class TimebasedFragmentationStrategy extends FragmentationStrategyDecorator {
	private final OpenFragmentProvider openFragmentProvider;
	private final Tracer tracer;

	public TimebasedFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			OpenFragmentProvider openFragmentProvider, Tracer tracer,
			TreeRelationsRepository treeRelationsRepository) {
		super(fragmentationStrategy, treeRelationsRepository);
		this.openFragmentProvider = openFragmentProvider;
		this.tracer = tracer;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Span parentSpan) {
		Span timebasedFragmentationSpan = tracer.nextSpan(parentSpan).name("timebased fragmentation").start();
		Pair<LdesFragment, Boolean> ldesFragment = openFragmentProvider
				.retrieveOpenFragmentOrCreateNewFragment(parentFragment);
		if (Boolean.TRUE.equals(ldesFragment.getRight())) {
			super.addRelationFromParentToChild(parentFragment, ldesFragment.getLeft());
		}
		super.addMemberToFragment(ldesFragment.getLeft(), member, timebasedFragmentationSpan);
		timebasedFragmentationSpan.end();
	}

}
