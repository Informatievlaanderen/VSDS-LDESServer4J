package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;

public class TimeBasedFragmentCreator implements FragmentCreator {

	public static final String DATE_TIME_TYPE = "http://www.w3.org/2001/XMLSchema#dateTime";
	protected final LdesConfig ldesConfig;
	protected final TimebasedFragmentationConfig timebasedFragmentationConfig;
	protected final LdesFragmentRepository ldesFragmentRepository;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	public TimeBasedFragmentCreator(LdesConfig ldesConfig, TimebasedFragmentationConfig timeBasedConfig,
			LdesFragmentRepository ldesFragmentRepository) {
		this.ldesConfig = ldesConfig;
		this.timebasedFragmentationConfig = timeBasedConfig;
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override
	public LdesFragment createNewFragment(Optional<LdesFragment> optionalLdesFragment,
			FragmentInfo parentFragmentInfo) {
		LdesFragment newFragment = createNewFragment(parentFragmentInfo);
		optionalLdesFragment
				.ifPresent(ldesFragment -> makeFragmentImmutableAndUpdateRelations(ldesFragment, newFragment));
		return newFragment;
	}

	@Override
	public boolean needsToCreateNewFragment(LdesFragment fragment) {
		return fragment.getCurrentNumberOfMembers() >= timebasedFragmentationConfig.getMemberLimit();
	}

	protected LdesFragment createNewFragment(FragmentInfo parentFragmentInfo) {
		List<FragmentPair> parentFragmentInfoFragmentPairs = parentFragmentInfo.getFragmentPairs();
		List<FragmentPair> fragmentPairs = updateParentFragmentPairs(parentFragmentInfoFragmentPairs);
		FragmentInfo fragmentInfo = new FragmentInfo(
				parentFragmentInfo.getViewName(), fragmentPairs);

		return new LdesFragment(
				LdesFragmentNamingStrategy.generateFragmentName(ldesConfig.getHostName(), fragmentInfo.getViewName(),
						fragmentInfo.getFragmentPairs()),
				fragmentInfo);
	}

	private List<FragmentPair> updateParentFragmentPairs(List<FragmentPair> parentFragmentInfoFragmentPairs) {
		List<FragmentPair> fragmentPairs = new ArrayList<>(parentFragmentInfoFragmentPairs.stream().toList());
		String fragmentationValue = LocalDateTime.now().format(formatter);
		fragmentPairs.add(new FragmentPair(GENERATED_AT_TIME, fragmentationValue));
		return fragmentPairs;
	}

	protected void makeFragmentImmutableAndUpdateRelations(LdesFragment completeLdesFragment,
			LdesFragment newFragment) {
		completeLdesFragment.setImmutable(true);
		completeLdesFragment.addRelation(new TreeRelation(GENERATED_AT_TIME,
				newFragment.getFragmentId(),
				newFragment.getFragmentInfo().getValueOfKey(GENERATED_AT_TIME).orElseThrow(
						() -> new MissingFragmentValueException(newFragment.getFragmentId(), GENERATED_AT_TIME)),
				DATE_TIME_TYPE,
				TREE_GREATER_THAN_OR_EQUAL_TO_RELATION));
		ldesFragmentRepository.saveFragment(completeLdesFragment);
		newFragment.addRelation(
				new TreeRelation(GENERATED_AT_TIME, completeLdesFragment.getFragmentId(),
						completeLdesFragment.getFragmentInfo().getValueOfKey(GENERATED_AT_TIME).orElseThrow(
								() -> new MissingFragmentValueException(completeLdesFragment.getFragmentId(),
										GENERATED_AT_TIME)),
						DATE_TIME_TYPE,
						TREE_LESSER_THAN_OR_EQUAL_TO_RELATION));
	}

}
