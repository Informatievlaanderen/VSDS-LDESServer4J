package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.treenoderelations.TreeNodeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.TileFragment;

import java.util.List;
import java.util.stream.Stream;

public class TileFragmentRelationsAttributer {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();

	private final TreeNodeRelationsRepository treeNodeRelationsRepository;

	public TileFragmentRelationsAttributer(LdesFragmentRepository ldesFragmentRepository, TreeNodeRelationsRepository treeNodeRelationsRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.treeNodeRelationsRepository = treeNodeRelationsRepository;
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
				.forEach(ldesFragment -> {
					TreeRelation relationToParentFragment = relationsAttributer.getRelationToParentFragment(
							ldesFragment);
					if(!treeNodeRelationsRepository.getRelations(tileRootFragment.getFragmentId()).contains(relationToParentFragment)){
						treeNodeRelationsRepository.addTreeNodeRelation(tileRootFragment.getFragmentId(), relationToParentFragment);
					}
				});
	}
}
