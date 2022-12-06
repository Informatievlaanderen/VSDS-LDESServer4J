package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.treenoderelations.TreeNodeRelationsRepository;

import java.util.List;
import java.util.stream.Stream;

public class TileFragmentRelationsAttributer {

	private final GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();

	private final TreeNodeRelationsRepository treeNodeRelationsRepository;

	public TileFragmentRelationsAttributer(TreeNodeRelationsRepository treeNodeRelationsRepository) {
		this.treeNodeRelationsRepository = treeNodeRelationsRepository;
	}

	public Stream<LdesFragment> addRelationsFromRootToBottom(LdesFragment rootFragment,
			List<LdesFragment> tileFragments) {
		addRelationsFromRootToCreatedTiles(rootFragment, tileFragments);
		return tileFragments
				.stream();
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
