package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;

public class TileFragmentRelationsAttributer {

	private final GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();
	private final FragmentRepository fragmentRepository;

	public TileFragmentRelationsAttributer(FragmentRepository fragmentRepository) {
		this.fragmentRepository = fragmentRepository;
	}

	public void addRelationsFromRootToBottom(Fragment rootFragment, Fragment tileFragments) {
		TreeRelation relationToParentFragment = tileFragments.getValueOfKey(FRAGMENT_KEY_TILE).orElse("").equals(DEFAULT_BUCKET_STRING) ? relationsAttributer.getDefaultRelation(tileFragments) : relationsAttributer.getRelationToParentFragment(tileFragments);
		if (!rootFragment.containsRelation(relationToParentFragment)) {
			rootFragment.addRelation(relationToParentFragment);
			fragmentRepository.saveFragment(rootFragment);
		}
	}
}
