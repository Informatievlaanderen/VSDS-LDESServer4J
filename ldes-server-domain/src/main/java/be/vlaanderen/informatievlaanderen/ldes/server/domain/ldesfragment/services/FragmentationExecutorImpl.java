package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FragmentationExecutorImpl implements FragmentationExecutor {

	private final Map<String, FragmentationService> fragmentationServices;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final LdesConfig ldesConfig;
	private final Tracer tracer;

	public FragmentationExecutorImpl(Map<String, FragmentationService> fragmentationServices,
			LdesFragmentRepository ldesFragmentRepository, LdesConfig ldesConfig, Tracer tracer) {
		this.fragmentationServices = fragmentationServices;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.ldesConfig = ldesConfig;
		this.tracer = tracer;
	}

	@Override
	public void executeFragmentation(String memberId) {
		fragmentationServices.forEach((key, fragmentationService) -> {
			Span rootFragmentRetrievalSpan = tracer.nextSpan().name("Root fragment retrieval").start();
			LdesFragment rootFragmentOfView = retrieveRootFragmentOfView(key);
			rootFragmentRetrievalSpan.end();
			fragmentationService.addMemberToFragment(rootFragmentOfView, memberId);
		});
	}

	private LdesFragment retrieveRootFragmentOfView(String viewName) {
		return ldesFragmentRepository
				.retrieveFragment(new LdesFragmentRequest(ldesConfig.getCollectionName(), viewName, List.of()))
				.orElseThrow(() -> new MissingRootFragmentException(ldesConfig.getCollectionName()));
	}
}
