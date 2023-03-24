package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

public class GeospatialFragmentCreator {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final TileFragmentRelationsAttributer tileFragmentRelationsAttributer;
	private static final Logger LOGGER = LoggerFactory.getLogger(GeospatialFragmentCreator.class);

	public GeospatialFragmentCreator(LdesFragmentRepository ldesFragmentRepository,
			TileFragmentRelationsAttributer tileFragmentRelationsAttributer) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.tileFragmentRelationsAttributer = tileFragmentRelationsAttributer;
	}

	public LdesFragment getOrCreateTileFragment(LdesFragment parentFragment, String tile,
			LdesFragment rootTileFragment) {
		LdesFragment child = parentFragment.createChild(new FragmentPair(FRAGMENT_KEY_TILE, tile));
		return ldesFragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					ldesFragmentRepository.saveFragment(child);
					tileFragmentRelationsAttributer
							.addRelationsFromRootToBottom(rootTileFragment, child);
					LOGGER.debug("Geospatial fragment created with id: {}", child.getFragmentId());
					return child;
				});
	}

	public LdesFragment getOrCreateRootFragment(LdesFragment parentFragment, String tile) {
		LdesFragment child = parentFragment.createChild(new FragmentPair(FRAGMENT_KEY_TILE, tile));
		return ldesFragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					ldesFragmentRepository.saveFragment(child);
					LOGGER.debug("Geospatial rootfragment created with id: {}", child.getFragmentId());
					return child;
				});
	}
}
