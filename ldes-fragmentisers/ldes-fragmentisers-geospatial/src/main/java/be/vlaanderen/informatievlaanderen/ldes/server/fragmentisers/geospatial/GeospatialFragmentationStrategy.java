package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.GeospatialRelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;

public class GeospatialFragmentationStrategy extends FragmentationStrategyDecorator {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final GeospatialBucketiser geospatialBucketiser;
	private final GeospatialFragmentCreator fragmentCreator;
	private final Tracer tracer;

	public GeospatialFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			LdesFragmentRepository ldesFragmentRepository,
			GeospatialBucketiser geospatialBucketiser, GeospatialFragmentCreator fragmentCreator, Tracer tracer) {
		super(fragmentationStrategy, ldesFragmentRepository);
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.geospatialBucketiser = geospatialBucketiser;
		this.fragmentCreator = fragmentCreator;
		this.tracer = tracer;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, LdesMember ldesMember, Span parentSpan) {
		Span geospatialFragmentationSpan = tracer.nextSpan(parentSpan).name("geospatial fragmentation").start();
		Set<String> tiles = geospatialBucketiser.bucketise(ldesMember);
		List<LdesFragment> ldesFragments = retrieveFragmentsOrCreateNewFragments(parentFragment,
				tiles);
		addRelationsToRootFragment(parentFragment, ldesFragments);
		ldesFragments.parallelStream().forEach(
				ldesFragment -> super.addMemberToFragment(ldesFragment, ldesMember, geospatialFragmentationSpan));
		geospatialFragmentationSpan.end();
	}

	private void addRelationsToRootFragment(LdesFragment parentFragment, List<LdesFragment> tileFragments) {
		LdesFragment tileRootFragment = fragmentCreator.getOrCreateGeospatialFragment(parentFragment,
				FRAGMENT_KEY_TILE_ROOT);

		super.addRelationFromParentToChild(parentFragment, tileRootFragment);

		GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer(ldesFragmentRepository);
		tileFragments.forEach(
				ldesFragment -> relationsAttributer.addRelationToParentFragment(tileRootFragment, ldesFragment));
		ldesFragmentRepository.saveFragment(tileRootFragment);
	}

	private List<LdesFragment> retrieveFragmentsOrCreateNewFragments(LdesFragment parentFragment,
			Set<String> tiles) {
		return tiles
				.stream().parallel().map(tile -> fragmentCreator.getOrCreateGeospatialFragment(parentFragment, tile))
				.toList();
	}
}
