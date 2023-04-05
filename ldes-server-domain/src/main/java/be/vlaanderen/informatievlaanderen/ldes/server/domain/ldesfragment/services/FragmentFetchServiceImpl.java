package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import org.springframework.stereotype.Component;

@Component
public class FragmentFetchServiceImpl implements FragmentFetchService {

	private final LdesConfig ldesConfig;
	private final LdesFragmentRepository ldesFragmentRepository;

	public FragmentFetchServiceImpl(LdesConfig ldesConfig,
			LdesFragmentRepository ldesFragmentRepository) {
		this.ldesConfig = ldesConfig;
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override
	public LdesFragment getFragment(LdesFragmentRequest ldesFragmentRequest) {
		return ldesFragmentRepository
				.retrieveFragment(ldesFragmentRequest)
				.orElseThrow(
						() -> new MissingFragmentException(

								ldesConfig.getHostName() + new FragmentInfo(ldesFragmentRequest.viewName(),
										ldesFragmentRequest.fragmentPairs()).generateFragmentId()));
	}
}
