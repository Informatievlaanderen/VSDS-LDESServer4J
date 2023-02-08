package be.vlaanderen.informatievlaanderen.vsds;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import org.apache.jena.rdf.model.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.PREVIOUS_PAGE_RELATION;

public class PageCreator {
	private final LdesFragmentRepository ldesFragmentRepository;
	private final TreeRelationsRepository treeRelationsRepository;
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor;

	private final Property fragmentationProperty;
	private static final Logger LOGGER = LoggerFactory.getLogger(PageCreator.class);

	public PageCreator(LdesFragmentRepository ldesFragmentRepository,
			TreeRelationsRepository treeRelationsRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor,
			Property fragmentationProperty) {
		this.treeRelationsRepository = treeRelationsRepository;

		this.ldesFragmentRepository = ldesFragmentRepository;
		this.nonCriticalTasksExecutor = nonCriticalTasksExecutor;
		this.fragmentationProperty = fragmentationProperty;
	}

	public LdesFragment createNewFragment(LdesFragment previousFragment) {
		String fragmentKey = fragmentationProperty.getLocalName();
		String fragmentValue = incrementStringNumber(previousFragment.getFragmentPairs().get(0).fragmentValue());
		LdesFragment newFragment = previousFragment.createChild(new FragmentPair(fragmentKey, fragmentValue));
		LOGGER.debug("Pagination fragment created with id: {}", newFragment.getFragmentId());
		makeFragmentImmutableAndUpdateRelations(previousFragment, newFragment);
		return newFragment;
	}

	private String incrementStringNumber(String number) {
		int incremented = Integer.parseInt(number) + 1;
		return String.valueOf(incremented);
	}

	private void makeFragmentImmutableAndUpdateRelations(LdesFragment completeLdesFragment,
			LdesFragment newFragment) {
		completeLdesFragment.makeImmutable();
		nonCriticalTasksExecutor
				.submit(() -> treeRelationsRepository.addTreeRelation(newFragment.getFragmentId(),
						new TreeRelation("", completeLdesFragment.getFragmentId(), "", "", NEXT_PAGE_RELATION)));
		ldesFragmentRepository.saveFragment(completeLdesFragment);
		nonCriticalTasksExecutor
				.submit(() -> treeRelationsRepository.addTreeRelation(completeLdesFragment.getFragmentId(),
						new TreeRelation("", newFragment.getFragmentId(), "", "", PREVIOUS_PAGE_RELATION)));
	}
}
