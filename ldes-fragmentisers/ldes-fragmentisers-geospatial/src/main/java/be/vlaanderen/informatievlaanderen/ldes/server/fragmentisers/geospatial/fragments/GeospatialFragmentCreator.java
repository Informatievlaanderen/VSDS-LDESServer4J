package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.TileFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

public class GeospatialFragmentCreator {

	private final LdesFragmentRepository ldesFragmentRepository;
	private ExecutorService executors;

	public GeospatialFragmentCreator(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.executors = Executors.newSingleThreadExecutor();
	}

	public TileFragment getOrCreateGeospatialFragment(LdesFragment parentFragment, String tile) {
		LdesFragment child = parentFragment.createChild(new FragmentPair(FRAGMENT_KEY_TILE, tile));
		return ldesFragmentRepository
				.retrieveFragment(child.getFragmentId())
				.map(ldesFragment -> new TileFragment(ldesFragment, false))
				.orElseGet(() -> {

					TileFragment tileFragment = new TileFragment(child, true);
					ldesFragmentRepository.saveFragment(child);
					return tileFragment;
				});
	}
}
