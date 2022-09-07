package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FragmentationExecutorImpl implements FragmentationExecutor {

	private final FragmentationService fragmentationService;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final LdesConfig ldesConfig;
	@Autowired
	private Tracer tracer;

	public FragmentationExecutorImpl(FragmentationService fragmentationService,
			LdesFragmentRepository ldesFragmentRepository, LdesConfig ldesConfig) {
		this.fragmentationService = fragmentationService;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.ldesConfig = ldesConfig;
	}

	@Override
	public void executeFragmentation(String memberId) {
		Span rootFragmentRetrievalSpan = tracer.nextSpan().name("Root fragment retrieval").start();
		LdesFragment rootFragment = retrieveRootFragment();
		rootFragmentRetrievalSpan.end();

		fragmentationService.addMemberToFragment(rootFragment, memberId);
	}

	private LdesFragment retrieveRootFragment() {
		return ldesFragmentRepository
				.retrieveFragment(new LdesFragmentRequest(ldesConfig.getCollectionName(), List.of()))
				.orElseThrow(() -> new MissingRootFragmentException(ldesConfig.getCollectionName()));
	}
}
