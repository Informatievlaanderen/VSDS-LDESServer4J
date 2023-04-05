package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;

public class TileFragmentRelationsAttributer {

	private final GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();

	private final TreeRelationsRepository treeRelationsRepository;

	public TileFragmentRelationsAttributer(TreeRelationsRepository treeRelationsRepository) {
		this.treeRelationsRepository = treeRelationsRepository;
	}

	public void addRelationsFromRootToBottom(LdesFragment rootFragment,
			LdesFragment tileFragments) {
		TreeRelation relationToParentFragment = relationsAttributer.getRelationToParentFragment(
				tileFragments);
		treeRelationsRepository.addTreeRelation(rootFragment.getFragmentId(), relationToParentFragment);
	}
}
