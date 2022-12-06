package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

@Component
public class ParentUpdaterImpl implements ParentUpdater {
	private final LdesFragmentRepository ldesFragmentRepository;

	public ParentUpdaterImpl(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	public void updateParent(LdesFragment currentChild) {
		List<FragmentPair> parentPairs = new ArrayList<>(currentChild.getFragmentInfo().getFragmentPairs());
		parentPairs.remove(parentPairs.size() - 1);
		LdesFragment parent = ldesFragmentRepository
				.retrieveMutableFragment(currentChild.getFragmentInfo().getViewName(), parentPairs)
				.orElseThrow(() -> new MissingFragmentException(
						new FragmentInfo(currentChild.getFragmentInfo().getViewName(), parentPairs)
								.generateFragmentId()));
		Optional<TreeRelation> optionalOldTreeRelation = parent.getRelations().stream()
				.filter(treeRelation -> treeRelation.treeNode().equals(currentChild.getFragmentId())).findFirst();
		if (optionalOldTreeRelation.isPresent()) {
			TreeRelation oldTreeRelation = optionalOldTreeRelation.get();
			LdesFragment newChild = ldesFragmentRepository
					.retrieveNonDeletedChildFragment(parent.getFragmentInfo().getViewName(), parentPairs)
					.orElseThrow(() -> new RuntimeException("No non-deleted child"));
//			parent.removeRelation(oldTreeRelation);
			TreeRelation newTreeRelation = new TreeRelation("", newChild.getFragmentId(), "", "",
					GENERIC_TREE_RELATION);
			ldesFragmentRepository.removeRelationFromFragment(parent, oldTreeRelation);
			ldesFragmentRepository.addRelationToFragment(parent, newTreeRelation);
		}

	}
}
