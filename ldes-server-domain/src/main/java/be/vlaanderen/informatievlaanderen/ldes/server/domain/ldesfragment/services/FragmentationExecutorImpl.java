package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingRootFragmentException;
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
	public synchronized void executeFragmentation(String memberId) {
		Span parentSpan = tracer.nextSpan().name("execute-fragmentation");
		parentSpan.start();
		fragmentationServices.entrySet().parallelStream().forEach(entry -> {
			LdesFragment rootFragmentOfView = retrieveRootFragmentOfView(entry.getKey(), parentSpan);
			entry.getValue().addMemberToFragment(rootFragmentOfView, memberId, parentSpan);
		});
		parentSpan.end();
	}

	private LdesFragment retrieveRootFragmentOfView(String viewName, Span name) {
		Span start = tracer.nextSpan(name).name("Retrieve root of view " + viewName).start();
		LdesFragment ldesFragment = ldesFragmentRepository
				.retrieveRootFragment(viewName)
				.orElseThrow(() -> new MissingRootFragmentException(viewName));
		start.end();
		return ldesFragment;
	}
}
