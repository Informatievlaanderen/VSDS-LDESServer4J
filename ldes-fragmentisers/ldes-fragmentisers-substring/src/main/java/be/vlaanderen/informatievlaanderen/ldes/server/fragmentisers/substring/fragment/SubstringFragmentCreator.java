package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.PaginationExecutorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubstringFragmentCreator {

	public static final String SUBSTRING = "substring";

	private final LdesFragmentRepository ldesFragmentRepository;
	private final PaginationExecutorImpl paginationExecutor;

	private static final Logger LOGGER = LoggerFactory.getLogger(SubstringFragmentCreator.class);

	public SubstringFragmentCreator(LdesFragmentRepository ldesFragmentRepository,
			PaginationExecutorImpl paginationExecutor) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.paginationExecutor = paginationExecutor;
	}

	public LdesFragment getOrCreateSubstringFragment(LdesFragment parentFragment, String substring) {
		LdesFragment child = parentFragment.createChild(new FragmentPair(SUBSTRING, substring));
		return ldesFragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					ldesFragmentRepository.saveFragment(child);
					paginationExecutor.linkFragments(child);
					LOGGER.debug("Substring fragment created with id: {}", child.getFragmentId());
					return child;
				});
	}
}
