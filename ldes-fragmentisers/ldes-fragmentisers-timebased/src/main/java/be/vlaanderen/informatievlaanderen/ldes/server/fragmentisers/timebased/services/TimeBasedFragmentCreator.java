package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.apache.jena.rdf.model.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_GREATER_THAN_OR_EQUAL_TO_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_LESSER_THAN_OR_EQUAL_TO_RELATION;

public class TimeBasedFragmentCreator {

	public static final String DATE_TIME_TYPE = "http://www.w3.org/2001/XMLSchema#dateTime";
	private final FragmentRepository fragmentRepository;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	private final Property fragmentationPath;
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeBasedFragmentCreator.class);

	public TimeBasedFragmentCreator(FragmentRepository fragmentRepository,
			Property fragmentationPath) {

		this.fragmentRepository = fragmentRepository;
		this.fragmentationPath = fragmentationPath;
	}

	public Fragment createNewFragment(Fragment parentFragment) {
		return createNewFragment(null, parentFragment);
	}

	public Fragment createNewFragment(Fragment fragment,
			Fragment parentFragment) {
		String fragmentKey = fragmentationPath.getLocalName();
		String fragmentValue = LocalDateTime.now().format(formatter);
		Fragment newFragment = parentFragment.createChild(new FragmentPair(fragmentKey, fragmentValue));
		LOGGER.debug("Time based fragment created with id: {}", newFragment.getFragmentId());
		if (fragment != null) {
			makeFragmentImmutableAndUpdateRelations(fragment, newFragment);
		}
		return newFragment;
	}

	private void makeFragmentImmutableAndUpdateRelations(Fragment completeFragment,
			Fragment newFragment) {
		completeFragment.makeImmutable();
		String treePath = fragmentationPath.toString();
		String fragmentKey = fragmentationPath.getLocalName();
		completeFragment
				.addRelation(new TreeRelation(treePath,
						newFragment.getFragmentId(),
						newFragment.getValueOfKey(fragmentKey).orElseThrow(
								() -> new MissingFragmentValueException(newFragment.getFragmentIdString(),
										fragmentKey)),
						DATE_TIME_TYPE,
						TREE_GREATER_THAN_OR_EQUAL_TO_RELATION));
		newFragment
				.addRelation(new TreeRelation(treePath, completeFragment.getFragmentId(),
						completeFragment.getValueOfKey(fragmentKey).orElseThrow(
								() -> new MissingFragmentValueException(completeFragment.getFragmentIdString(),
										fragmentKey)),
						DATE_TIME_TYPE,
						TREE_LESSER_THAN_OR_EQUAL_TO_RELATION));
		fragmentRepository.saveFragment(completeFragment);
		fragmentRepository.saveFragment(newFragment);
	}

}
