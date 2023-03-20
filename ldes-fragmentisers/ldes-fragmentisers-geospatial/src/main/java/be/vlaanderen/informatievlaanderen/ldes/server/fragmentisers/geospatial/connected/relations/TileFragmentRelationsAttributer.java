package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;

public class TileFragmentRelationsAttributer {

	private final GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();
	private final LdesFragmentRepository ldesFragmentRepository;

	public TileFragmentRelationsAttributer(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	public void addRelationsFromRootToBottom(LdesFragment rootFragment,
			LdesFragment tileFragments) {
		TreeRelation relationToParentFragment = relationsAttributer.getRelationToParentFragment(
				tileFragments);
		if (!rootFragment.containsRelation(relationToParentFragment)) {
			rootFragment.addRelation(relationToParentFragment);
			ldesFragmentRepository.saveFragment(rootFragment);
		}
	}
}
