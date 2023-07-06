package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import org.apache.jena.rdf.model.Model;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

public abstract class FragmentationStrategyDecorator implements FragmentationStrategy {

	private final FragmentationStrategy fragmentationStrategy;

	private final FragmentRepository fragmentRepository;

	protected FragmentationStrategyDecorator(FragmentationStrategy fragmentationStrategy,
			FragmentRepository fragmentRepository) {
		this.fragmentationStrategy = fragmentationStrategy;
		this.fragmentRepository = fragmentRepository;
	}

	@Override
	public void addMemberToFragment(LdesFragment rootFragmentOfView, String memberId, Model memberModel,
			Observation parentObservation) {
		fragmentationStrategy.addMemberToFragment(rootFragmentOfView, memberId, memberModel, parentObservation);
	}

	protected void addRelationFromParentToChild(LdesFragment parentFragment, LdesFragment childFragment) {
		TreeRelation treeRelation = new TreeRelation("", childFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION);
		if (!parentFragment.containsRelation(treeRelation)) {
			parentFragment.addRelation(treeRelation);
			fragmentRepository.saveFragment(parentFragment);
		}
	}

}
