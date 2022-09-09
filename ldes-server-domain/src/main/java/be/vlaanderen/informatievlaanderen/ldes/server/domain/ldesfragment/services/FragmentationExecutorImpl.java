package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FragmentationExecutorImpl implements FragmentationExecutor {

	private final Map<String, FragmentationService> fragmentationServices;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final Tracer tracer;

	public FragmentationExecutorImpl(Map<String, FragmentationService> fragmentationServices,
			LdesFragmentRepository ldesFragmentRepository, Tracer tracer) {
		this.fragmentationServices = fragmentationServices;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.tracer = tracer;
	}

	@Override
	public void executeFragmentation(String memberId) {
		fragmentationServices.entrySet().stream().parallel().forEach(entry -> {
			Span rootFragmentRetrievalSpan = tracer.nextSpan().name("Root fragment retrieval").start();
			LdesFragment rootFragmentOfView = retrieveRootFragmentOfView(entry.getKey());
			rootFragmentRetrievalSpan.end();
			entry.getValue().addMemberToFragment(rootFragmentOfView, memberId);
		});
	}

	private LdesFragment retrieveRootFragmentOfView(String viewName) {
		return ldesFragmentRepository
				.retrieveRootFragment(viewName)
				.orElseThrow(() -> new MissingRootFragmentException(viewName));
	}
}
