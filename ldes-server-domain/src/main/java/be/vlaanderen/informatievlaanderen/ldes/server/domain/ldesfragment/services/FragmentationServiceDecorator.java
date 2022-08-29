package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;

import java.util.List;
import java.util.Optional;

public abstract class FragmentationServiceDecorator implements FragmentationService {

	private final FragmentationService fragmentationService;
	private final LdesFragmentRepository ldesFragmentRepository;

	private final LdesConfig ldesConfig;

	protected FragmentationServiceDecorator(FragmentationService fragmentationService,
			LdesFragmentRepository ldesFragmentRepository, LdesConfig ldesConfig) {
		this.fragmentationService = fragmentationService;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.ldesConfig = ldesConfig;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, String ldesMemberId) {
		fragmentationService.addMemberToFragment(retrieveParentFragment(parentFragment), ldesMemberId);
	}

	private LdesFragment retrieveParentFragment(LdesFragment parentFragment) {
		return Optional.ofNullable(parentFragment)
				.orElseGet(() -> ldesFragmentRepository.retrieveFragment(new LdesFragmentRequest(ldesConfig.getCollectionName(), List.of()))
				.orElseThrow(() -> new MissingRootFragmentException(ldesConfig.getCollectionName())));
	}
}
