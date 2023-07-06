package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;

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
			ObservationRegistry observationRegistry, FragmentRepository fragmentRepository) {
		super(fragmentationStrategy, fragmentRepository);
		this.geospatialBucketiser = geospatialBucketiser;
		this.fragmentCreator = fragmentCreator;
		this.observationRegistry = observationRegistry;
	}

	@Override public void addMemberToFragment(LdesFragment parentFragment, String memberId, Model memberModel,
			Observation parentObservation) {
		Observation geospatialFragmentationObservation = Observation.createNotStarted("geospatial fragmentation",
						observationRegistry)
				.parentObservation(parentObservation)
				.start();
		getRootTileFragment(parentFragment);
		Set<String> tiles = geospatialBucketiser.bucketise(memberModel);
		List<LdesFragment> ldesFragments = tiles
				.stream()
				.map(tile -> fragmentCreator.getOrCreateTileFragment(parentFragment, tile, rootTileFragment)).toList();
		ldesFragments
				.parallelStream()
				.forEach(ldesFragment -> super.addMemberToFragment(ldesFragment, memberId, memberModel,
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
