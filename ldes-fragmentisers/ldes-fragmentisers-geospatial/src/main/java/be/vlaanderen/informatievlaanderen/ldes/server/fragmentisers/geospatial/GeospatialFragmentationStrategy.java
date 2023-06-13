package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;

public class GeospatialFragmentationStrategy extends FragmentationStrategyDecorator {
	public static final String GEOSPATIAL_FRAGMENTATION = "GeospatialFragmentation";
	private final GeospatialBucketiser geospatialBucketiser;
	private final GeospatialFragmentCreator fragmentCreator;
	private final ObservationRegistry observationRegistry;

	private LdesFragment rootTileFragment = null;

	public GeospatialFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			GeospatialBucketiser geospatialBucketiser, GeospatialFragmentCreator fragmentCreator,
			ObservationRegistry observationRegistry, LdesFragmentRepository ldesFragmentRepository) {
		super(fragmentationStrategy, ldesFragmentRepository);
		this.geospatialBucketiser = geospatialBucketiser;
		this.fragmentCreator = fragmentCreator;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Observation parentObservation) {
		Observation geospatialFragmentationObservation = Observation.createNotStarted("geospatial fragmentation",
				observationRegistry)
				.parentObservation(parentObservation)
				.start();
		getRootTileFragment(parentFragment);
		Set<String> tiles = geospatialBucketiser.bucketise(member);
		List<LdesFragment> ldesFragments = tiles
				.stream()
				.map(tile -> fragmentCreator.getOrCreateTileFragment(parentFragment, tile, rootTileFragment)).toList();
		ldesFragments
				.parallelStream()
				.forEach(ldesFragment -> super.addMemberToFragment(ldesFragment, member,
						geospatialFragmentationObservation));
		geospatialFragmentationObservation.stop();
	}

	private void getRootTileFragment(LdesFragment parentFragment) {
		if (rootTileFragment == null) {
			LdesFragment tileRootFragment = fragmentCreator.getOrCreateRootFragment(parentFragment,
					FRAGMENT_KEY_TILE_ROOT);
			super.addRelationFromParentToChild(parentFragment, tileRootFragment);
			rootTileFragment = tileRootFragment;
		}
	}
}
