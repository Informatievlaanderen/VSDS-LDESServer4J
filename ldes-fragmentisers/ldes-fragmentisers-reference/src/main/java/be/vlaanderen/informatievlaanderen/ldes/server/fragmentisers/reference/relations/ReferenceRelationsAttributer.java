package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingFragmentValueException;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceFragmentCreator.FRAGMENT_KEY_REFERENCE;

public class ReferenceRelationsAttributer {

	public static final String TREE_REFERENCE_EQUALS_RELATION = TREE + "EqualToRelation";

	public TreeRelation getRelationToParentFragment(Fragment childFragment) {
		String treeValue = getTreeValue(childFragment);

		// TODO TVB: 18/01/24 ask ranko value type?
		var treeValueType = "";

		// TODO TVB: 18/01/24 use fragmentation path from config
		var treePath = "<http://example.com>/<http://example.org>";

		return new TreeRelation(treePath, childFragment.getFragmentId(),
				treeValue, treeValueType, TREE_REFERENCE_EQUALS_RELATION);

	}

	private String getTreeValue(Fragment currentFragment) {
		return currentFragment
				.getValueOfKey(FRAGMENT_KEY_REFERENCE)
				.orElseThrow(
						() -> new MissingFragmentValueException(currentFragment.getFragmentIdString(), FRAGMENT_KEY_REFERENCE)
				);
	}
}
