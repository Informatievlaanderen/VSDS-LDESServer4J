package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;

public class TileFragmentRelationsAttributer {

	private final GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();
	private final FragmentRepository fragmentRepository;

	public TileFragmentRelationsAttributer(FragmentRepository fragmentRepository) {
		this.fragmentRepository = fragmentRepository;
	}

	public void addRelationsFromRootToBottom(Fragment rootFragment,
			Fragment tileFragments) {
		TreeRelation relationToParentFragment = relationsAttributer.getRelationToParentFragment(
				tileFragments);
		if (!rootFragment.containsRelation(relationToParentFragment)) {
			rootFragment.addRelation(relationToParentFragment);
			fragmentRepository.saveFragment(rootFragment);
		}
	}
}
