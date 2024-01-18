package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;

public class ReferenceFragmentRelationsAttributer {

	private final ReferenceRelationsAttributer relationsAttributer = new ReferenceRelationsAttributer();
	private final FragmentRepository fragmentRepository;

	public ReferenceFragmentRelationsAttributer(FragmentRepository fragmentRepository) {
		this.fragmentRepository = fragmentRepository;
	}

	public void addRelationsFromRootToBottom(Fragment rootFragment, Fragment referenceFragments) {
		TreeRelation relationToParentFragment = relationsAttributer.getRelationToParentFragment(referenceFragments);
		if (!rootFragment.containsRelation(relationToParentFragment)) {
			rootFragment.addRelation(relationToParentFragment);
			fragmentRepository.saveFragment(rootFragment);
		}
	}
}
