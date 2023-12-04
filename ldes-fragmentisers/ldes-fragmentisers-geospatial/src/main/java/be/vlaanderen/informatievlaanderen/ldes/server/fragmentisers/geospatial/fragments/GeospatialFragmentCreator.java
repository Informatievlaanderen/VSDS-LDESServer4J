package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.CounterConstants.LDES_SERVER_CREATE_FRAGMENTS_COUNT;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

public class GeospatialFragmentCreator {

	private final FragmentRepository fragmentRepository;
	private final TileFragmentRelationsAttributer tileFragmentRelationsAttributer;
	private static final Logger LOGGER = LoggerFactory.getLogger(GeospatialFragmentCreator.class);

	public GeospatialFragmentCreator(FragmentRepository fragmentRepository,
			TileFragmentRelationsAttributer tileFragmentRelationsAttributer) {
		this.fragmentRepository = fragmentRepository;
		this.tileFragmentRelationsAttributer = tileFragmentRelationsAttributer;
	}

	public Fragment getOrCreateTileFragment(Fragment parentFragment, String tile,
			Fragment rootTileFragment) {
		Fragment child = parentFragment.createChild(new FragmentPair(FRAGMENT_KEY_TILE, tile));
		return fragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					fragmentRepository.saveFragment(child);
					tileFragmentRelationsAttributer
							.addRelationsFromRootToBottom(rootTileFragment, child);
					String viewName = parentFragment.getViewName().asString();
					Metrics.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT, "view", viewName, "fragmentation-strategy", "geospatial").increment();
					LOGGER.debug("Geospatial fragment created with id: {}", child.getFragmentId());
					return child;
				});
	}

	public Fragment getOrCreateRootFragment(Fragment parentFragment, String tile) {
		Fragment child = parentFragment.createChild(new FragmentPair(FRAGMENT_KEY_TILE, tile));
		return fragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					fragmentRepository.saveFragment(child);
					String viewName = parentFragment.getViewName().asString();
					Metrics.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT, "view", viewName, "fragmentation-strategy", "geospatial").increment();
					LOGGER.debug("Geospatial rootfragment created with id: {}", child.getFragmentId());
					return child;
				});
	}
}
