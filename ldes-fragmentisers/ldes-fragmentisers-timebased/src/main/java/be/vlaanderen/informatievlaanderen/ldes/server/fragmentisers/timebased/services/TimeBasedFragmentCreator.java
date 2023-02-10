package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import org.apache.jena.rdf.model.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_GREATER_THAN_OR_EQUAL_TO_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_LESSER_THAN_OR_EQUAL_TO_RELATION;

public class TimeBasedFragmentCreator {

	public static final String DATE_TIME_TYPE = "http://www.w3.org/2001/XMLSchema#dateTime";
	private final LdesFragmentRepository ldesFragmentRepository;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private final TreeRelationsRepository treeRelationsRepository;
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor;

	private final Property fragmentationProperty;
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeBasedFragmentCreator.class);

	public TimeBasedFragmentCreator(LdesFragmentRepository ldesFragmentRepository,
			TreeRelationsRepository treeRelationsRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor,
			Property fragmentationProperty) {
		this.treeRelationsRepository = treeRelationsRepository;

		this.ldesFragmentRepository = ldesFragmentRepository;
		this.nonCriticalTasksExecutor = nonCriticalTasksExecutor;
		this.fragmentationProperty = fragmentationProperty;
	}

	public LdesFragment createNewFragment(LdesFragment parentFragment) {
		return createNewFragment(null, parentFragment);
	}

	public LdesFragment createNewFragment(LdesFragment ldesFragment,
			LdesFragment parentFragment) {
		String fragmentKey = fragmentationProperty.getLocalName();
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
		String treePath = fragmentationProperty.toString();
		String fragmentKey = fragmentationProperty.getLocalName();
		nonCriticalTasksExecutor
				.submit(() -> treeRelationsRepository.addTreeRelation(completeLdesFragment.getFragmentId(),
						new TreeRelation(treePath,
								newFragment.getFragmentId(),
								newFragment.getValueOfKey(fragmentKey).orElseThrow(
										() -> new MissingFragmentValueException(newFragment.getFragmentId(),
												fragmentKey)),
								DATE_TIME_TYPE,
								TREE_GREATER_THAN_OR_EQUAL_TO_RELATION)));
		ldesFragmentRepository.saveFragment(completeLdesFragment);
		nonCriticalTasksExecutor
				.submit(() -> treeRelationsRepository.addTreeRelation(newFragment.getFragmentId(),
						new TreeRelation(treePath, completeLdesFragment.getFragmentId(),
								completeLdesFragment.getValueOfKey(fragmentKey).orElseThrow(
										() -> new MissingFragmentValueException(completeLdesFragment.getFragmentId(),
												fragmentKey)),
								DATE_TIME_TYPE,
								TREE_LESSER_THAN_OR_EQUAL_TO_RELATION)));
	}

}
