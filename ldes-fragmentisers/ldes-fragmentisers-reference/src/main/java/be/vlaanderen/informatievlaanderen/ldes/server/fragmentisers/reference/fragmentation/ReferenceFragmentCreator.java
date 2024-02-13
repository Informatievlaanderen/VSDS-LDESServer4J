package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations.ReferenceFragmentRelationsAttributer;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.LDES_SERVER_CREATE_FRAGMENTS_COUNT;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.FRAGMENTATION_STRATEGY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.VIEW;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.ReferenceFragmentationStrategy.REFERENCE_FRAGMENTATION;

public class ReferenceFragmentCreator {

	private final String fragmentKeyReference;
	public static final String FRAGMENT_KEY_REFERENCE_ROOT = "";
	private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceFragmentCreator.class);
	private final FragmentRepository fragmentRepository;
	private final ReferenceFragmentRelationsAttributer relationsAttributer;

	public ReferenceFragmentCreator(FragmentRepository fragmentRepository,
									ReferenceFragmentRelationsAttributer relationsAttributer,
									String fragmentKeyReference) {
		this.fragmentRepository = fragmentRepository;
        this.relationsAttributer = relationsAttributer;
		this.fragmentKeyReference = fragmentKeyReference;
    }

	public Fragment getOrCreateFragment(Fragment parentFragment, String reference, Fragment rootFragment) {
		Fragment child = parentFragment.createChild(new FragmentPair(fragmentKeyReference, reference));
		return fragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					fragmentRepository.saveFragment(child);
					if (reference.equals(DEFAULT_BUCKET_STRING)) {
						relationsAttributer.addDefaultRelation(parentFragment, child);
					} else {
						relationsAttributer.addRelationsFromRootToBottom(rootFragment, child);
					}
					logFragmentation(parentFragment, child);
					return child;
				});
	}

	public Fragment getOrCreateRootFragment(Fragment parentFragment, String reference) {
		Fragment child = parentFragment.createChild(new FragmentPair(fragmentKeyReference, reference));
		return fragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					fragmentRepository.saveFragment(child);
					logFragmentation(parentFragment, child);
					return child;
				});
	}

	private void logFragmentation(Fragment parentFragment, Fragment child) {
		String viewName = parentFragment.getViewName().asString();
		Metrics.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT,
				VIEW, viewName, FRAGMENTATION_STRATEGY, REFERENCE_FRAGMENTATION).increment();
		LOGGER.debug("Reference fragment created with id: {}", child.getFragmentId());
	}

}
