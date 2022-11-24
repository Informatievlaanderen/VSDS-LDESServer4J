package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.TileFragment;

import java.util.List;
import java.util.stream.Stream;

public class TileFragmentRelationsAttributer {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();

	public TileFragmentRelationsAttributer(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	public Stream<LdesFragment> addRelationsFromRootToBottom(LdesFragment rootFragment,
			List<TileFragment> tileFragments) {
		addRelationsFromRootToCreatedTiles(rootFragment, getCreatedTiles(tileFragments));
		return tileFragments
				.stream()
				.map(TileFragment::ldesFragment);
	}

	private List<LdesFragment> getCreatedTiles(List<TileFragment> tileFragments) {
		return tileFragments
				.stream()
				.filter(TileFragment::created)
				.map(TileFragment::ldesFragment)
				.toList();
	}

	private void addRelationsFromRootToCreatedTiles(LdesFragment tileRootFragment, List<LdesFragment> tileFragments) {
		tileFragments.stream()
				.parallel()
				.forEach(ldesFragment -> relationsAttributer.addRelationToParentFragment(tileRootFragment,
						ldesFragment));
		ldesFragmentRepository.saveFragment(tileRootFragment);
	}
}
