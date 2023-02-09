package be.vlaanderen.informatievlaanderen.vsds;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.vsds.services.OpenPageProvider;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.commons.lang3.tuple.ImmutablePair;

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
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Observation parentObservation) {
		Observation paginationObservation = Observation.createNotStarted("pagination",
				observationRegistry)
				.parentObservation(parentObservation)
				.start();
		ImmutablePair<LdesFragment, Boolean> ldesFragment = openPageProvider
				.retrieveOpenFragmentOrCreateNewFragment(parentFragment);
		if (Boolean.TRUE.equals(ldesFragment.getRight())) {
			super.addRelationFromParentToChild(parentFragment, ldesFragment.getLeft());
		}
		super.addMemberToFragment(ldesFragment.getLeft(), member, paginationObservation);
		paginationObservation.stop();
	}

}
