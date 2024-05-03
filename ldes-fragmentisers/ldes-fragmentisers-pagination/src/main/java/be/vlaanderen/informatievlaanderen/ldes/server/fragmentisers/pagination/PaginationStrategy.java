package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.OpenPageProvider;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Boolean.TRUE;

public class PaginationStrategy extends FragmentationStrategyDecorator {
	private static final Logger log = LoggerFactory.getLogger(PaginationStrategy.class);

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
	public void addMemberToFragment(Fragment parentFragment, String memberId, Model memberModel,
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
		super.addMemberToFragment(ldesFragment.getLeft(), memberId, memberModel, paginationObservation);
		paginationObservation.stop();
		log.debug("Member {} to be paginated in fragment {}", memberId, ldesFragment.getLeft().getFragmentId().asDecodedFragmentId());
	}
}
