package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.treenoderelations.TreeNodeRelationsRepository;

public class TileFragmentRelationsAttributer {

	private final GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();

	private final TreeNodeRelationsRepository treeNodeRelationsRepository;

	public TileFragmentRelationsAttributer(TreeNodeRelationsRepository treeNodeRelationsRepository) {
		this.treeNodeRelationsRepository = treeNodeRelationsRepository;
	}

	public void addRelationsFromRootToBottom(LdesFragment rootFragment,
			LdesFragment tileFragments) {
		TreeRelation relationToParentFragment = relationsAttributer.getRelationToParentFragment(
				tileFragments);
		if (!treeNodeRelationsRepository.getRelations(rootFragment.getFragmentId())
				.contains(relationToParentFragment)) {
			treeNodeRelationsRepository.addTreeNodeRelation(rootFragment.getFragmentId(), relationToParentFragment);
		}
	}
}
