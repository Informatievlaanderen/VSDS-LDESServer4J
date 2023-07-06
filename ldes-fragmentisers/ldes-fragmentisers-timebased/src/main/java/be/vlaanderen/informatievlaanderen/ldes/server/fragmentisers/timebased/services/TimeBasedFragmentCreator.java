package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
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

	public LdesFragment createNewFragment(LdesFragment parentFragment) {
		return createNewFragment(null, parentFragment);
	}

	public LdesFragment createNewFragment(LdesFragment ldesFragment,
			LdesFragment parentFragment) {
		String fragmentKey = fragmentationPath.getLocalName();
		String fragmentValue = LocalDateTime.now().format(formatter);
		LdesFragment newFragment = parentFragment.createChild(new FragmentPair(fragmentKey, fragmentValue));
		LOGGER.debug("Time based fragment created with id: {}", newFragment.getFragmentId());
		if (ldesFragment != null) {
			makeFragmentImmutableAndUpdateRelations(ldesFragment, newFragment);
		}
		return newFragment;
	}

	private void makeFragmentImmutableAndUpdateRelations(LdesFragment completeLdesFragment,
			LdesFragment newFragment) {
		completeLdesFragment.makeImmutable();
		String treePath = fragmentationPath.toString();
		String fragmentKey = fragmentationPath.getLocalName();
		completeLdesFragment
				.addRelation(new TreeRelation(treePath,
						newFragment.getFragmentId(),
						newFragment.getValueOfKey(fragmentKey).orElseThrow(
								() -> new MissingFragmentValueException(newFragment.getFragmentIdString(),
										fragmentKey)),
						DATE_TIME_TYPE,
						TREE_GREATER_THAN_OR_EQUAL_TO_RELATION));
		newFragment
				.addRelation(new TreeRelation(treePath, completeLdesFragment.getFragmentId(),
						completeLdesFragment.getValueOfKey(fragmentKey).orElseThrow(
								() -> new MissingFragmentValueException(completeLdesFragment.getFragmentIdString(),
										fragmentKey)),
						DATE_TIME_TYPE,
						TREE_LESSER_THAN_OR_EQUAL_TO_RELATION));
		fragmentRepository.saveFragment(completeLdesFragment);
		fragmentRepository.saveFragment(newFragment);
	}

}
