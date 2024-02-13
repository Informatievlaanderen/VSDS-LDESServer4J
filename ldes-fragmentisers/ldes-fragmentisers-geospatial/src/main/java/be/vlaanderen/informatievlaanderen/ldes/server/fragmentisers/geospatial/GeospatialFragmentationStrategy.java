package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;

public class GeospatialFragmentationStrategy extends FragmentationStrategyDecorator {
	public static final String GEOSPATIAL_FRAGMENTATION = "GeospatialFragmentation";
	private final GeospatialBucketiser geospatialBucketiser;
	private final GeospatialFragmentCreator fragmentCreator;
	private final ObservationRegistry observationRegistry;

	private Fragment rootTileFragment = null;

	public GeospatialFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			GeospatialBucketiser geospatialBucketiser, GeospatialFragmentCreator fragmentCreator,
			ObservationRegistry observationRegistry, FragmentRepository fragmentRepository) {
		super(fragmentationStrategy, fragmentRepository);
		this.geospatialBucketiser = geospatialBucketiser;
		this.fragmentCreator = fragmentCreator;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public void addMemberToFragment(Fragment parentFragment, String memberId, Model memberModel,
			Observation parentObservation) {
		Observation geospatialFragmentationObservation = Observation.createNotStarted("geospatial fragmentation",
				observationRegistry)
				.parentObservation(parentObservation)
				.start();
		getRootTileFragment(parentFragment);

		Set<String> tiles = geospatialBucketiser.bucketise(memberId, memberModel);

		List<Fragment> fragments = tiles
				.stream()
				.map(tile -> {
					if (tile.equals(DEFAULT_BUCKET_STRING)) {
						return fragmentCreator.getOrCreateTileFragment(parentFragment, tile, parentFragment);
					} else {
						return fragmentCreator.getOrCreateTileFragment(parentFragment, tile, rootTileFragment);
					}
				}).toList();

		fragments
				.parallelStream()
				.forEach(ldesFragment -> super.addMemberToFragment(ldesFragment, memberId, memberModel,
						geospatialFragmentationObservation));
		geospatialFragmentationObservation.stop();
	}

	private void getRootTileFragment(Fragment parentFragment) {
		if (rootTileFragment == null) {
			Fragment tileRootFragment = fragmentCreator.getOrCreateRootFragment(parentFragment,
					FRAGMENT_KEY_TILE_ROOT);
			super.addRelationFromParentToChild(parentFragment, tileRootFragment);
			rootTileFragment = tileRootFragment;
		}
	}
}
