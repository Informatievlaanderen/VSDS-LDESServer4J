package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.SubstringConstants.STRING_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.SubstringConstants.TREE_SUBSTRING_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator.SUBSTRING;

public class SubstringRelationsAttributer {

	private final LdesFragmentRepository ldesFragmentRepository;

	public SubstringRelationsAttributer(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	public void addSubstringRelation(LdesFragment parentFragment, LdesFragment childFragment) {

		String substringValue = getSubstringValue(childFragment);
		TreeRelation parentChildRelation = new TreeRelation(null, childFragment.getFragmentId(),
				substringValue, STRING_TYPE,
				TREE_SUBSTRING_RELATION);

		ldesFragmentRepository.addRelationToFragment(parentFragment, parentChildRelation);
	}

	private String getSubstringValue(LdesFragment childFragment) {
		return childFragment.getFragmentInfo().getValueOfKey(SUBSTRING).map(substring -> substring.replace("\"", ""))
				.orElseThrow(
						() -> new MissingFragmentValueException(childFragment.getFragmentId(), SUBSTRING));

	}
}
