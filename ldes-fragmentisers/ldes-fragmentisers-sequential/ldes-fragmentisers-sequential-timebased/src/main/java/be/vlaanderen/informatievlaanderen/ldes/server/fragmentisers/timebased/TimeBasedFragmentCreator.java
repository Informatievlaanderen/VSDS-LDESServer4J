package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services.TimestampPathComparator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;

public class TimeBasedFragmentCreator implements FragmentCreator {

	public static final String DATE_TIME_TYPE = "http://www.w3.org/2001/XMLSchema#dateTime";
	protected final LdesConfig ldesConfig;
	protected final SequentialFragmentationConfig sequentialFragmentationConfig;
	protected final LdesMemberRepository ldesMemberRepository;
	protected final LdesFragmentRepository ldesFragmentRepository;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");


	public TimeBasedFragmentCreator(LdesConfig ldesConfig, SequentialFragmentationConfig timeBasedConfig,
									LdesMemberRepository ldesMemberRepository,
			LdesFragmentRepository ldesFragmentRepository) {
		this.ldesConfig = ldesConfig;
		this.sequentialFragmentationConfig = timeBasedConfig;
		this.ldesMemberRepository = ldesMemberRepository;
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override
	public LdesFragment createNewFragment(Optional<LdesFragment> optionalLdesFragment, List<FragmentPair> bucket) {
		LdesFragment newFragment = createNewFragment(bucket);
		optionalLdesFragment
				.ifPresent(ldesFragment -> makeFragmentImmutableAndUpdateRelations(ldesFragment, newFragment));
		return newFragment;
	}

	@Override
	public boolean needsToCreateNewFragment(LdesFragment fragment) {
		return fragment.getCurrentNumberOfMembers() >= sequentialFragmentationConfig.getMemberLimit();
	}

	protected LdesFragment createNewFragment(List<FragmentPair> bucket) {
		List<FragmentPair> fragmentPairs = new ArrayList<>(bucket.stream().toList());
		String fragmentationValue = LocalDateTime.now().format(formatter);
		fragmentPairs.add( new FragmentPair(GENERATED_AT_TIME, fragmentationValue));
		FragmentInfo fragmentInfo = new FragmentInfo(
				ldesConfig.getCollectionName(),
				fragmentPairs);

		return new LdesFragment(LdesFragmentNamingStrategy.generateFragmentName(ldesConfig, fragmentInfo),
				fragmentInfo);
	}

	protected void makeFragmentImmutableAndUpdateRelations(LdesFragment completeLdesFragment,
			LdesFragment newFragment) {
		completeLdesFragment.setImmutable(true);
		completeLdesFragment.addRelation(new TreeRelation(GENERATED_AT_TIME,
				newFragment.getFragmentId(), newFragment.getFragmentInfo().getFragmentPairs().stream().filter(fragmentPair -> fragmentPair.fragmentKey().equals("generatedAtTime")).map(FragmentPair::fragmentValue).findFirst().get(), DATE_TIME_TYPE,
				TREE_GREATER_THAN_OR_EQUAL_TO_RELATION));
		String latestGeneratedAtTime = getLatestGeneratedAtTime(completeLdesFragment);
		ldesFragmentRepository.saveFragment(completeLdesFragment);
		newFragment.addRelation(
				new TreeRelation(GENERATED_AT_TIME, completeLdesFragment.getFragmentId(),
						latestGeneratedAtTime, DATE_TIME_TYPE, TREE_LESSER_THAN_OR_EQUAL_TO_RELATION));
	}

	private String getLatestGeneratedAtTime(LdesFragment completeLdesFragment) {
		return ldesMemberRepository.getLdesMembersByIds(completeLdesFragment.getMemberIds())
				.max(new TimestampPathComparator())
				.map(ldesMember -> ldesMember.getFragmentationObject(PROV_GENERATED_AT_TIME).toString())
				.orElseGet(() -> LocalDateTime.now().format(sequentialFragmentationConfig.getDatetimeFormatter()));
	}

}
