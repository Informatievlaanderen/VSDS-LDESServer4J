package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.OpenPageProvider;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

import static java.lang.Boolean.TRUE;

public class PaginationStrategy extends FragmentationStrategyDecorator {

	public static final String PAGINATION_FRAGMENTATION = "PaginationFragmentation";

	private final OpenPageProvider openPageProvider;

	private final ObservationRegistry observationRegistry;

	public PaginationStrategy(FragmentationStrategy fragmentationStrategy,
			OpenPageProvider openPageProvider, ObservationRegistry observationRegistry,
			FragmentRepository fragmentRepository) {
		super(fragmentationStrategy, fragmentRepository);
		this.openPageProvider = openPageProvider;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public List<BucketisedMember> addMemberToFragment(Fragment parentFragment, Member member,
													  Observation parentObservation) {
		Observation paginationObservation = Observation.createNotStarted(PAGINATION_FRAGMENTATION,
				observationRegistry)
				.parentObservation(parentObservation)
				.start();
		ImmutablePair<Fragment, Boolean> ldesFragment = openPageProvider
				.retrieveOpenFragmentOrCreateNewFragment(parentFragment);
		if (TRUE.equals(ldesFragment.getRight())) {
			super.addRelationFromParentToChild(parentFragment, ldesFragment.getLeft());
		}
		List<BucketisedMember> members = super.addMemberToFragment(ldesFragment.getLeft(), member, paginationObservation);
		paginationObservation.stop();
		return members;
	}
}
