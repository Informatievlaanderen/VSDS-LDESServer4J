package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.OpenPageProvider;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.commons.lang3.tuple.ImmutablePair;

import static java.lang.Boolean.TRUE;

public class PaginationStrategy extends FragmentationStrategyDecorator {
	public static final String PAGINATION = "PaginationFragmentation";

	private final OpenPageProvider openPageProvider;

	private final ObservationRegistry observationRegistry;

	public PaginationStrategy(FragmentationStrategy fragmentationStrategy,
			OpenPageProvider openPageProvider, ObservationRegistry observationRegistry,
			LdesFragmentRepository ldesFragmentRepository) {
		super(fragmentationStrategy, ldesFragmentRepository);
		this.openPageProvider = openPageProvider;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Observation parentObservation) {
		Observation paginationObservation = Observation.createNotStarted(PAGINATION,
				observationRegistry)
				.parentObservation(parentObservation)
				.start();
		ImmutablePair<LdesFragment, Boolean> ldesFragment = openPageProvider
				.retrieveOpenFragmentOrCreateNewFragment(parentFragment);
		if (TRUE.equals(ldesFragment.getRight())) {
			super.addRelationFromParentToChild(parentFragment, ldesFragment.getLeft());
		}
		super.addMemberToFragment(ldesFragment.getLeft(), member, paginationObservation);
		paginationObservation.stop();
	}

}
