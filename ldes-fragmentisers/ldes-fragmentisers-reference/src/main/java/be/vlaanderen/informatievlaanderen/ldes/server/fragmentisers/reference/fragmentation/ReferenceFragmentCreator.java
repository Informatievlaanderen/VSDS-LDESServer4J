package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.LDES_SERVER_CREATE_FRAGMENTS_COUNT;

public class ReferenceFragmentCreator {

	public static final String FRAGMENT_KEY_REFERENCE = "reference";
	private final FragmentRepository fragmentRepository;
	private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceFragmentCreator.class);

	public ReferenceFragmentCreator(FragmentRepository fragmentRepository) {
		this.fragmentRepository = fragmentRepository;
	}

	public Fragment getOrCreateFragment(Fragment parentFragment, String reference) {
		Fragment child = parentFragment.createChild(new FragmentPair(FRAGMENT_KEY_REFERENCE, reference));
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
				"view", viewName, "fragmentation-strategy", FRAGMENT_KEY_REFERENCE).increment();
		LOGGER.debug("Reference fragment created with id: {}", child.getFragmentId());
	}

}
