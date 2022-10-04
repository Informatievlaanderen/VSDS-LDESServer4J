package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.bucketiser.SubstringBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentFinder;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.List;

public class SubstringFragmentationStrategy extends FragmentationStrategyDecorator {

	private final Tracer tracer;
	private final SubstringBucketiser substringBucketiser;
	private final SubstringFragmentFinder substringFragmentFinder;
	private final SubstringFragmentCreator substringFragmentCreator;

	public SubstringFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			LdesFragmentRepository ldesFragmentRepository,
			Tracer tracer,
			SubstringBucketiser substringBucketiser,
			SubstringFragmentFinder substringFragmentFinder, SubstringFragmentCreator substringFragmentCreator) {
		super(fragmentationStrategy, ldesFragmentRepository);
		this.tracer = tracer;
		this.substringBucketiser = substringBucketiser;
		this.substringFragmentFinder = substringFragmentFinder;
		this.substringFragmentCreator = substringFragmentCreator;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, LdesMember ldesMember, Span parentSpan) {
		Span substringFragmentationSpan = tracer.nextSpan(parentSpan).name("substring fragmentation").start();
		List<String> buckets = substringBucketiser.bucketise(ldesMember);
		LdesFragment rootFragment = substringFragmentCreator.getOrCreateSubstringFragment(parentFragment, "");
		super.addRelationFromParentToChild(parentFragment, rootFragment);
		LdesFragment substringFragment = substringFragmentFinder
				.getOpenLdesFragmentOrLastPossibleFragment(parentFragment, rootFragment, buckets);
		super.addMemberToFragment(substringFragment, ldesMember, substringFragmentationSpan);
		substringFragmentationSpan.end();
	}

}
