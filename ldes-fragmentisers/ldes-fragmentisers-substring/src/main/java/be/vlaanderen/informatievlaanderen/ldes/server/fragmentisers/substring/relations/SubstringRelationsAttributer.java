package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.SubstringConstants.STRING_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.SubstringConstants.TREE_SUBSTRING_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator.SUBSTRING;

public class SubstringRelationsAttributer {

	private final TreeRelationsRepository treeRelationsRepository;
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor;
	private final SubstringConfig substringConfig;

	public SubstringRelationsAttributer(TreeRelationsRepository treeRelationsRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor,
			SubstringConfig substringConfig) {
		this.treeRelationsRepository = treeRelationsRepository;
		this.nonCriticalTasksExecutor = nonCriticalTasksExecutor;
		this.substringConfig = substringConfig;
	}

	public void addSubstringRelation(LdesFragment parentFragment, LdesFragment childFragment) {
		String substringValue = getSubstringValue(childFragment);
		TreeRelation parentChildRelation = new TreeRelation(substringConfig.getFragmenterPropertyQuery(),
				childFragment.getFragmentId(),
				substringValue, STRING_TYPE,
				TREE_SUBSTRING_RELATION);
		nonCriticalTasksExecutor.submit(() -> treeRelationsRepository
				.addTreeRelation(parentFragment.getFragmentId(), parentChildRelation));
	}

	private String getSubstringValue(LdesFragment childFragment) {
		return childFragment.getFragmentInfo().getValueOfKey(SUBSTRING).map(substring -> substring.replace("\"", ""))
				.orElseThrow(
						() -> new MissingFragmentValueException(childFragment.getFragmentId(), SUBSTRING));

	}
}
