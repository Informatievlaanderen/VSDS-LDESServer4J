package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

public class GeospatialFragmentCreator {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final TileFragmentRelationsAttributer tileFragmentRelationsAttributer;
	private final ExecutorService executor;

	public GeospatialFragmentCreator(LdesFragmentRepository ldesFragmentRepository, TileFragmentRelationsAttributer tileFragmentRelationsAttributer) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.tileFragmentRelationsAttributer = tileFragmentRelationsAttributer;
		this.executor = Executors.newSingleThreadExecutor();
	}

	public LdesFragment getOrCreateGeospatialFragment(LdesFragment parentFragment, String tile, LdesFragment rootTileFragment) {
		LdesFragment child = parentFragment.createChild(new FragmentPair(FRAGMENT_KEY_TILE, tile));
		return ldesFragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					ldesFragmentRepository.saveFragment(child);
					executor.submit(()->tileFragmentRelationsAttributer.addRelationsFromRootToBottom(rootTileFragment, child));
					return child;
				});
	}
}
