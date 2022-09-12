package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;

import java.util.List;
import java.util.Optional;

public class FragmentationServiceImpl implements FragmentationService {
	private final LdesFragmentRepository ldesFragmentRepository;
	private final LdesConfig ldesConfig;

	public FragmentationServiceImpl(LdesFragmentRepository ldesFragmentRepository,
			LdesConfig ldesConfig, String name) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.ldesConfig = ldesConfig;
		addRootFragment(name);
	}

	private void addRootFragment(String viewName) {
		Optional<LdesFragment> optionalRoot = ldesFragmentRepository
				.retrieveRootFragment(viewName);
		if (optionalRoot.isEmpty()) {
			createRoot(viewName);
		}
	}

	private void createRoot(String viewName) {
		FragmentInfo fragmentInfo = new FragmentInfo(
				viewName, List.of());
		LdesFragment ldesFragment = new LdesFragment(
				LdesFragmentNamingStrategy.generateFragmentName(ldesConfig, fragmentInfo),
				fragmentInfo);
		ldesFragmentRepository.saveFragment(ldesFragment);
	}

	@Override
	public void addMemberToFragment(LdesFragment ldesFragment, String ldesMemberId) {

		ldesFragment.addMember(ldesMemberId);
		ldesFragmentRepository.saveFragment(ldesFragment);

	}

}
