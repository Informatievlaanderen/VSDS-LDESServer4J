package be.vlaanderen.informatievlaanderen.vsds;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

public class PaginationStrategy extends FragmentationStrategyDecorator {
	private final OpenPageProvider openPageProvider;

	private final ObservationRegistry observationRegistry;

	public PaginationStrategy(FragmentationStrategy fragmentationStrategy,
			OpenPageProvider openPageProvider, ObservationRegistry observationRegistry,
			TreeRelationsRepository treeRelationsRepository) {
		super(fragmentationStrategy, treeRelationsRepository);
		this.openPageProvider = openPageProvider;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public void addMemberToFragment(LdesFragment lastFragment, Member member, Observation parentObservation) {
		Observation paginationObservation = Observation.createNotStarted("pagination",
				observationRegistry)
				.parentObservation(parentObservation)
				.start();
		LdesFragment currentFragment = openPageProvider
				.retrieveOpenFragmentOrCreateNewFragment(lastFragment);
		/*
		 * if (Boolean.TRUE.equals(currentFragment.getRight())) {
		 * super.addRelationFromParentToChild(lastFragment, currentFragment.getLeft());
		 * }
		 */
		super.addMemberToFragment(currentFragment, member, paginationObservation);
		paginationObservation.stop();
	}

}
