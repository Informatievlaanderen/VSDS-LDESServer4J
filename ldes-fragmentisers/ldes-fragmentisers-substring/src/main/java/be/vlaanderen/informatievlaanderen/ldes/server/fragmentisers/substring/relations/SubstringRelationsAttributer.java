package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.SubstringConstants.STRING_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.SubstringConstants.TREE_SUBSTRING_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator.SUBSTRING;

public class SubstringRelationsAttributer {

	private final LdesFragmentRepository ldesFragmentRepository;

	private final SubstringConfig substringConfig;

	public SubstringRelationsAttributer(LdesFragmentRepository ldesFragmentRepository,
			SubstringConfig substringConfig) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.substringConfig = substringConfig;
	}

	public void addSubstringRelation(LdesFragment parentFragment, LdesFragment childFragment) {
		String substringValue = getSubstringValue(childFragment);
		TreeRelation parentChildRelation = new TreeRelation(substringConfig.getFragmenterProperty(),
				childFragment.getFragmentId(),
				substringValue, STRING_TYPE,
				TREE_SUBSTRING_RELATION);
		if (!parentFragment.containsRelation(parentChildRelation)) {
			parentFragment.addRelation(parentChildRelation);
			ldesFragmentRepository.saveFragment(parentFragment);
		}
	}

	private String getSubstringValue(LdesFragment childFragment) {
		return childFragment.getValueOfKey(SUBSTRING).map(substring -> substring.replace("\"", ""))
				.orElseThrow(
						() -> new MissingFragmentValueException(childFragment.getFragmentId(), SUBSTRING));

	}
}
