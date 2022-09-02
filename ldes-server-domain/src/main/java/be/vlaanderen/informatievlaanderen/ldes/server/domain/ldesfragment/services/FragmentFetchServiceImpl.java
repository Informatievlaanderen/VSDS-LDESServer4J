package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import org.springframework.stereotype.Component;

import java.util.List;

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
				.orElseGet(() -> createEmptyFragment(ldesFragmentRequest.collectionName(),
						ldesFragmentRequest.viewName(), ldesFragmentRequest.fragmentPairs()));
	}

	@Override
	public LdesFragment getInitialFragment(LdesFragmentRequest ldesFragmentRequest) {
		return ldesFragmentRepository
				.retrieveInitialFragment(ldesFragmentRequest.collectionName())
				.orElseGet(
						() -> createEmptyFragment(ldesFragmentRequest.collectionName(), ldesFragmentRequest.viewName(),
								ldesFragmentRequest.fragmentPairs()));

	}

	private LdesFragment createEmptyFragment(String collectionName, String viewName,
			List<FragmentPair> fragmentationMap) {
		FragmentInfo fragmentInfo = new FragmentInfo(collectionName, viewName, fragmentationMap);

		return new LdesFragment(LdesFragmentNamingStrategy.generateFragmentName(ldesConfig, fragmentInfo),
				fragmentInfo);
	}
}
