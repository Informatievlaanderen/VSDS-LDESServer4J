package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.TileFragment;

import java.util.List;

public class TileFragmentRelationsAttributer {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();

	public TileFragmentRelationsAttributer(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	public List<LdesFragment> addRelationsFromRootToBottom(LdesFragment rootFragment,
			List<TileFragment> tileFragments) {
		addRelationsFromRootToCreatedTiles(rootFragment, getCreatedTiles(tileFragments));
		return tileFragments
				.parallelStream()   // TODO: is parallelisation worth the effort here? TileFragment::ldesFragment seems to be quite lightweight.
				.map(TileFragment::ldesFragment)
				.toList();
	}

	private List<LdesFragment> getCreatedTiles(List<TileFragment> tileFragments) {
		return tileFragments
				.stream()
				.filter(TileFragment::created)
				.map(TileFragment::ldesFragment)
				.toList();
	}

	private void addRelationsFromRootToCreatedTiles(LdesFragment tileRootFragment, List<LdesFragment> tileFragments) {
		tileFragments.forEach(
				ldesFragment -> relationsAttributer.addRelationToParentFragment(tileRootFragment, ldesFragment));
		ldesFragmentRepository.saveFragment(tileRootFragment);
	}
}
