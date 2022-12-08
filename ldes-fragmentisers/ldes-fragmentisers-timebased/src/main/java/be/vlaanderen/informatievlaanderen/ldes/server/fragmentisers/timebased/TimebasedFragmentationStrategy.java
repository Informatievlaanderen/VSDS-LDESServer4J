package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.treenoderelations.TreeNodeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.OpenFragmentProvider;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

public class TimebasedFragmentationStrategy extends FragmentationStrategyDecorator {
	private final OpenFragmentProvider openFragmentProvider;
	private final Tracer tracer;
	private final TreeNodeRelationsRepository treeNodeRelationsRepository;
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor;

	public TimebasedFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			OpenFragmentProvider openFragmentProvider, Tracer tracer,
			TreeNodeRelationsRepository treeNodeRelationsRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor) {
		super(fragmentationStrategy, treeNodeRelationsRepository);
		this.openFragmentProvider = openFragmentProvider;
		this.tracer = tracer;
		this.treeNodeRelationsRepository = treeNodeRelationsRepository;
		this.nonCriticalTasksExecutor = nonCriticalTasksExecutor;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Span parentSpan) {
		Span timebasedFragmentationSpan = tracer.nextSpan(parentSpan).name("timebased fragmentation").start();
		LdesFragment ldesFragment = openFragmentProvider.retrieveOpenFragmentOrCreateNewFragment(parentFragment);
		nonCriticalTasksExecutor.submit(() -> {
			if (treeNodeRelationsRepository.getRelations(parentFragment.getFragmentId()).stream()
					.noneMatch(relation -> relation.relation().equals(GENERIC_TREE_RELATION))) {
				super.addRelationFromParentToChild(parentFragment, ldesFragment);
			}
		});
		super.addMemberToFragment(ldesFragment, member, timebasedFragmentationSpan);
		timebasedFragmentationSpan.end();
	}

}
