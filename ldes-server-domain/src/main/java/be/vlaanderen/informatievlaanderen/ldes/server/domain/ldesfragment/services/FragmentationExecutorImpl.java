package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FragmentationExecutorImpl implements FragmentationExecutor {

	private final Map<String, FragmentationService> fragmentationServices;
	private final LdesFragmentRepository ldesFragmentRepository;

	public FragmentationExecutorImpl(Map<String, FragmentationService> fragmentationServices,
			LdesFragmentRepository ldesFragmentRepository) {
		this.fragmentationServices = fragmentationServices;
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override
	public void executeFragmentation(String memberId) {
		fragmentationServices.entrySet().stream().parallel().forEach(entry -> {
			LdesFragment rootFragmentOfView = retrieveRootFragmentOfView(entry.getKey());
			entry.getValue().addMemberToFragment(rootFragmentOfView, memberId);
		});
	}

	private LdesFragment retrieveRootFragmentOfView(String viewName) {
		return ldesFragmentRepository
				.retrieveRootFragment(viewName)
				.orElseThrow(() -> new MissingRootFragmentException(viewName));
	}
}
