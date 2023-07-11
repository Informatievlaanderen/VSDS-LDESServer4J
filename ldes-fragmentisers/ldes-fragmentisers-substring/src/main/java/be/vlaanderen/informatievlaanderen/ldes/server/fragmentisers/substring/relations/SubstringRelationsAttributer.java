package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.SubstringConstants.STRING_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.SubstringConstants.TREE_SUBSTRING_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator.SUBSTRING;

public class SubstringRelationsAttributer {

	private final FragmentRepository fragmentRepository;

	private final SubstringConfig substringConfig;

	public SubstringRelationsAttributer(FragmentRepository fragmentRepository,
			SubstringConfig substringConfig) {
		this.fragmentRepository = fragmentRepository;
		this.substringConfig = substringConfig;
	}

	public void addSubstringRelation(Fragment parentFragment, Fragment childFragment) {
		String substringValue = getSubstringValue(childFragment);
		TreeRelation parentChildRelation = new TreeRelation(substringConfig.getFragmentationPath(),
				childFragment.getFragmentId(),
				substringValue, STRING_TYPE,
				TREE_SUBSTRING_RELATION);
		if (!parentFragment.containsRelation(parentChildRelation)) {
			parentFragment.addRelation(parentChildRelation);
			fragmentRepository.saveFragment(parentFragment);
		}
	}

	private String getSubstringValue(Fragment childFragment) {
		return childFragment.getValueOfKey(SUBSTRING).map(substring -> substring.replace("\"", ""))
				.orElseThrow(
						() -> new MissingFragmentValueException(childFragment.getFragmentIdString(), SUBSTRING));

	}
}
