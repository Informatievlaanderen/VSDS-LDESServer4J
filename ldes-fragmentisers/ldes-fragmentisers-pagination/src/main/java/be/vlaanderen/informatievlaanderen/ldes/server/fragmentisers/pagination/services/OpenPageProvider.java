package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

public class OpenPageProvider {
	private final PageCreator pageCreator;
	private final FragmentRepository fragmentRepository;
	private final Long memberLimit;

	public OpenPageProvider(PageCreator pageCreator,
			FragmentRepository fragmentRepository, Long memberLimit) {
		this.pageCreator = pageCreator;
		this.fragmentRepository = fragmentRepository;
		this.memberLimit = memberLimit;
	}

	public Fragment retrieveOpenFragmentOrCreateNewFragment(LdesFragmentIdentifier parentId) {

		return fragmentRepository
				.retrieveOpenChildFragment(parentId)
				.map(fragment -> {
					if (needsToCreateNewFragment(fragment)) {
						Fragment parentFragment = fragmentRepository.retrieveFragment(parentId).orElseThrow();
						Fragment newFragment = pageCreator.createNewFragment(fragment, parentFragment);
						fragmentRepository.saveFragment(newFragment);
						return newFragment;
					} else {
						return fragment;
					}
				})
				.orElseGet(() -> {
					Fragment parentFragment = fragmentRepository.retrieveFragment(parentId).orElseThrow();
					Fragment newFragment = pageCreator.createFirstFragment(parentFragment);
					fragmentRepository.saveFragment(newFragment);

					TreeRelation treeRelation = new TreeRelation("", newFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION);
					if (!parentFragment.containsRelation(treeRelation)) {
						parentFragment.addRelation(treeRelation);
						fragmentRepository.saveFragment(parentFragment);
					}

					return newFragment;
				});
	}

	public boolean needsToCreateNewFragment(Fragment fragment) {
		return fragment.getNrOfMembersAdded() >= memberLimit;
	}
}
