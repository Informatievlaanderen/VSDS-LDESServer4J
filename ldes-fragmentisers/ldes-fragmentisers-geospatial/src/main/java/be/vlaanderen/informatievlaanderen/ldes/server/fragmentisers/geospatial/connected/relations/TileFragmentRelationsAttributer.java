package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;

public class TileFragmentRelationsAttributer {

	private final GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();
	private final FragmentRepository fragmentRepository;

	public TileFragmentRelationsAttributer(FragmentRepository fragmentRepository) {
		this.fragmentRepository = fragmentRepository;
	}

	public void addRelationsFromRootToBottom(LdesFragment rootFragment,
			LdesFragment tileFragments) {
		TreeRelation relationToParentFragment = relationsAttributer.getRelationToParentFragment(
				tileFragments);
		if (!rootFragment.containsRelation(relationToParentFragment)) {
			rootFragment.addRelation(relationToParentFragment);
			fragmentRepository.saveFragment(rootFragment);
		}
	}
}
