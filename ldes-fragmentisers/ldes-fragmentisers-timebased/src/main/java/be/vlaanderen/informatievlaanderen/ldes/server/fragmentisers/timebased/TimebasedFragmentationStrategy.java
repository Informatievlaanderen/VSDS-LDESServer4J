package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.OpenFragmentProvider;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

public class TimebasedFragmentationStrategy extends FragmentationStrategyDecorator {
	private final OpenFragmentProvider openFragmentProvider;
	private final Tracer tracer;
	private final TreeRelationsRepository treeRelationsRepository;
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor;

	public TimebasedFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			OpenFragmentProvider openFragmentProvider, Tracer tracer,
			TreeRelationsRepository treeRelationsRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor) {
		super(fragmentationStrategy, treeRelationsRepository);
		this.openFragmentProvider = openFragmentProvider;
		this.tracer = tracer;
		this.treeRelationsRepository = treeRelationsRepository;
		this.nonCriticalTasksExecutor = nonCriticalTasksExecutor;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Span parentSpan) {
		Span timebasedFragmentationSpan = tracer.nextSpan(parentSpan).name("timebased fragmentation").start();
		LdesFragment ldesFragment = openFragmentProvider.retrieveOpenFragmentOrCreateNewFragment(parentFragment);
		nonCriticalTasksExecutor.submit(() -> {
			if (treeRelationsRepository.getRelations(parentFragment.getFragmentId()).stream()
					.noneMatch(relation -> relation.relation().equals(GENERIC_TREE_RELATION))) {
				super.addRelationFromParentToChild(parentFragment, ldesFragment);
			}
		});
		super.addMemberToFragment(ldesFragment, member, timebasedFragmentationSpan);
		timebasedFragmentationSpan.end();
	}

}
