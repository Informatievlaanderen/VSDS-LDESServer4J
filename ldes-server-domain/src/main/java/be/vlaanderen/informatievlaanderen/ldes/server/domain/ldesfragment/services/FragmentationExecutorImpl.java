package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FragmentationExecutorImpl implements FragmentationExecutor {

	private final Map<String, FragmentationStrategy> fragmentationStrategyMap;
	private final Map<String, LdesFragment> rootFragmentMap;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final Tracer tracer;

	public FragmentationExecutorImpl(Map<String, FragmentationStrategy> fragmentationStrategyMap,
									 LdesFragmentRepository ldesFragmentRepository, Tracer tracer) {
		this.fragmentationStrategyMap = fragmentationStrategyMap;
		this.rootFragmentMap = new HashMap<>();
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.tracer = tracer;
	}

	@Override
	public synchronized void executeFragmentation(Member member) {
		Span parentSpan = tracer.nextSpan()
				.name("execute fragmentation")
				.tag("memberId", member.getLdesMemberId())
				.start();
		fragmentationStrategyMap.entrySet().parallelStream().forEach(entry -> {
			LdesFragment rootFragmentOfView = retrieveRootFragmentOfView(entry.getKey(), parentSpan);
			entry.getValue().addMemberToFragment(rootFragmentOfView, member, parentSpan);
		});
		parentSpan.end();
	}

	private LdesFragment retrieveRootFragmentOfView(String viewName, Span name) {
		Span start = tracer.nextSpan(name).name("retrieve root of view " + viewName).start();

		LdesFragment ldesFragment = rootFragmentMap.computeIfAbsent(viewName, s -> ldesFragmentRepository
				.retrieveRootFragment(viewName)
				.orElseThrow(() -> new MissingRootFragmentException(viewName)));

		start.end();
		return ldesFragment;
	}
}
