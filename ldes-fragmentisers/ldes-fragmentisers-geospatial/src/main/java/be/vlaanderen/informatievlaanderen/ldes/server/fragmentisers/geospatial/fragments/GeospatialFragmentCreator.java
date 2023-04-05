package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.TileFragment;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

public class GeospatialFragmentCreator {

	private final LdesFragmentRepository ldesFragmentRepository;

	public GeospatialFragmentCreator(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	public TileFragment getOrCreateGeospatialFragment(LdesFragment parentFragment, String tile) {
		LdesFragment child = parentFragment.createChild(new FragmentPair(FRAGMENT_KEY_TILE, tile));
		return ldesFragmentRepository
				.retrieveFragment(new LdesFragmentRequest(
						child.getFragmentInfo().getViewName(),
						child.getFragmentInfo().getFragmentPairs()))
				.map(ldesFragment -> new TileFragment(ldesFragment, false))
				.orElseGet(() -> new TileFragment(child, true));
	}
}
