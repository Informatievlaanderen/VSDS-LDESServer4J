package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.relations.RelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE;

public class ReferenceFragmentRelationsAttributer implements RelationsAttributer {

	public static final String TREE_REFERENCE_EQUALS_RELATION = TREE + "EqualToRelation";

	private final FragmentRepository fragmentRepository;
	private final String fragmentationPath;
	private final String fragmentKeyReference;

	public ReferenceFragmentRelationsAttributer(FragmentRepository fragmentRepository,
												String fragmentationPath,
												String fragmentKeyReference) {
		this.fragmentRepository = fragmentRepository;
		this.fragmentationPath = fragmentationPath;
		this.fragmentKeyReference = fragmentKeyReference;
	}

	public void addRelationsFromRootToBottom(Fragment rootFragment, Fragment referenceFragments) {
		saveRelation(rootFragment, getRelationToParentFragment(referenceFragments));
	}

	public void addDefaultRelation(Fragment rootFragment, Fragment fragment) {
		saveRelation(rootFragment, getDefaultRelation(fragment));
	}

	private void saveRelation(Fragment fragment, TreeRelation relation) {
		if (!fragment.containsRelation(relation)) {
			fragment.addRelation(relation);
			fragmentRepository.saveFragment(fragment);
		}
	}

	private TreeRelation getRelationToParentFragment(Fragment childFragment) {
		return new TreeRelation(fragmentationPath, childFragment.getFragmentId(),
				getTreeValue(childFragment), XSDDatatype.XSDanyURI.getURI(), TREE_REFERENCE_EQUALS_RELATION);
	}

	private String getTreeValue(Fragment currentFragment) {
		return currentFragment
				.getValueOfKey(fragmentKeyReference)
				.orElseThrow(
						() -> new MissingFragmentValueException(currentFragment.getFragmentIdString(), fragmentKeyReference)
				);
	}
}
