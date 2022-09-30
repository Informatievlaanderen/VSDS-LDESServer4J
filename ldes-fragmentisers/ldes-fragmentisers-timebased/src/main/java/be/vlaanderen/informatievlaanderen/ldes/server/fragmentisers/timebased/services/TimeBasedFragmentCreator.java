package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedFragmentationConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;

public class TimeBasedFragmentCreator {

	public static final String DATE_TIME_TYPE = "http://www.w3.org/2001/XMLSchema#dateTime";
	private final TimebasedFragmentationConfig timebasedFragmentationConfig;
	private final LdesFragmentRepository ldesFragmentRepository;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	public TimeBasedFragmentCreator(TimebasedFragmentationConfig timeBasedConfig,
			LdesFragmentRepository ldesFragmentRepository) {
		this.timebasedFragmentationConfig = timeBasedConfig;
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	public LdesFragment createNewFragment(Optional<LdesFragment> optionalLdesFragment,
			LdesFragment parentFragment) {
		String fragmentationValue = LocalDateTime.now().format(formatter);
		LdesFragment newFragment = parentFragment.createChild(new FragmentPair(GENERATED_AT_TIME, fragmentationValue));

		optionalLdesFragment
				.ifPresent(ldesFragment -> makeFragmentImmutableAndUpdateRelations(ldesFragment, newFragment));
		return newFragment;
	}

	public boolean needsToCreateNewFragment(LdesFragment fragment) {
		return fragment.getCurrentNumberOfMembers() >= timebasedFragmentationConfig.getMemberLimit();
	}

	private void makeFragmentImmutableAndUpdateRelations(LdesFragment completeLdesFragment,
			LdesFragment newFragment) {
		completeLdesFragment.setImmutable(true);
		completeLdesFragment.addRelation(new TreeRelation(PROV_GENERATED_AT_TIME,
				newFragment.getFragmentId(),
				newFragment.getFragmentInfo().getValueOfKey(GENERATED_AT_TIME).orElseThrow(
						() -> new MissingFragmentValueException(newFragment.getFragmentId(), GENERATED_AT_TIME)),
				DATE_TIME_TYPE,
				TREE_GREATER_THAN_OR_EQUAL_TO_RELATION));
		ldesFragmentRepository.saveFragment(completeLdesFragment);
		newFragment.addRelation(
				new TreeRelation(PROV_GENERATED_AT_TIME, completeLdesFragment.getFragmentId(),
						completeLdesFragment.getFragmentInfo().getValueOfKey(GENERATED_AT_TIME).orElseThrow(
								() -> new MissingFragmentValueException(completeLdesFragment.getFragmentId(),
										GENERATED_AT_TIME)),
						DATE_TIME_TYPE,
						TREE_LESSER_THAN_OR_EQUAL_TO_RELATION));
	}

}
