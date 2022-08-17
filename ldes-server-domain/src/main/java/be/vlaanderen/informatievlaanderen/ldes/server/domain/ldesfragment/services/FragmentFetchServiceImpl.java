package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FragmentFetchServiceImpl implements FragmentFetchService {

	private final LdesConfig ldesConfig;
	private final LdesFragmentNamingStrategy ldesFragmentNamingStrategy;
	private final LdesFragmentRepository ldesFragmentRepository;

	public FragmentFetchServiceImpl(LdesConfig ldesConfig, LdesFragmentNamingStrategy ldesFragmentNamingStrategy,
			LdesFragmentRepository ldesFragmentRepository) {
		this.ldesConfig = ldesConfig;
		this.ldesFragmentNamingStrategy = ldesFragmentNamingStrategy;
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override
	public LdesFragment getFragment(LdesFragmentRequest ldesFragmentRequest) {
		return ldesFragmentRepository
				.retrieveFragment(ldesFragmentRequest)
				.orElseGet(() -> createEmptyFragment(ldesFragmentRequest.collectionName(),
						ldesFragmentRequest.fragmentPairs()));
	}

	@Override
	public LdesFragment getInitialFragment(LdesFragmentRequest ldesFragmentRequest) {
		return ldesFragmentRepository
				.retrieveInitialFragment(ldesFragmentRequest.collectionName())
				.orElseGet(() -> createEmptyFragment(ldesFragmentRequest.collectionName(),
						ldesFragmentRequest.fragmentPairs()));

	}

	private LdesFragment createEmptyFragment(String collectionName, List<FragmentPair> fragmentationMap) {
		FragmentInfo fragmentInfo = new FragmentInfo(collectionName, fragmentationMap);

		return new LdesFragment(ldesFragmentNamingStrategy.generateFragmentName(ldesConfig, fragmentInfo),
				fragmentInfo);
	}
}
