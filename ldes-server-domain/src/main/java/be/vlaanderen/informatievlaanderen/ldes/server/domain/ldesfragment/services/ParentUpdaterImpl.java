package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

@Component
public class ParentUpdaterImpl implements ParentUpdater {
	private final LdesFragmentRepository ldesFragmentRepository;

	private final TreeRelationsRepository treeRelationsRepository;

	public ParentUpdaterImpl(LdesFragmentRepository ldesFragmentRepository,
			TreeRelationsRepository treeRelationsRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.treeRelationsRepository = treeRelationsRepository;
	}

	public void updateParent(LdesFragment currentChild) {
		String childId = currentChild.getFragmentId();
		String parentId = currentChild.getParentId();
		List<FragmentPair> parentPairs = new ArrayList<>(currentChild.getFragmentPairs());
		parentPairs.remove(parentPairs.size() - 1);
		LdesFragment parent = ldesFragmentRepository
				.retrieveMutableFragment(currentChild.getViewName(), parentPairs)
				.orElseThrow(() -> new MissingFragmentException(
						new LdesFragment(currentChild.getViewName(), parentPairs).getFragmentId()));

		List<TreeRelation> relations = treeRelationsRepository.getRelations(parentId);
		Optional<TreeRelation> optionalOldTreeRelation = relations.stream()
				.filter(treeRelation -> treeRelation.treeNode().equals(childId)).findFirst();
		if (optionalOldTreeRelation.isPresent()) {
			TreeRelation oldTreeRelation = optionalOldTreeRelation.get();
			LdesFragment newChild = ldesFragmentRepository
					.retrieveNonDeletedChildFragment(parent.getViewName(),
							parentPairs)
					.orElseThrow(() -> new RuntimeException("No non-deleted child"));
			treeRelationsRepository.deleteTreeRelation(parentId, oldTreeRelation);
			treeRelationsRepository.addTreeRelation(parentId, new TreeRelation("", newChild.getFragmentId(), "", "",
					GENERIC_TREE_RELATION));
		}
	}
}
