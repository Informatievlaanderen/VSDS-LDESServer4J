package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.TileFragment;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;

public class GeospatialFragmentationStrategy extends FragmentationStrategyDecorator {

	private final GeospatialBucketiser geospatialBucketiser;
	private final GeospatialFragmentCreator fragmentCreator;
	private final TileFragmentRelationsAttributer tileFragmentRelationsAttributer;
	private final ObservationRegistry observationRegistry;

	public GeospatialFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			LdesFragmentRepository ldesFragmentRepository,
			GeospatialBucketiser geospatialBucketiser, GeospatialFragmentCreator fragmentCreator,
			TileFragmentRelationsAttributer tileFragmentRelationsAttributer, ObservationRegistry observationRegistry) {
		super(fragmentationStrategy, ldesFragmentRepository);
		this.geospatialBucketiser = geospatialBucketiser;
		this.fragmentCreator = fragmentCreator;
		this.tileFragmentRelationsAttributer = tileFragmentRelationsAttributer;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, Member member, Observation parentObservation) {
		Observation geospatialFragmentationSpan = Observation.createNotStarted("geospatial fragmentation",
						observationRegistry)
				.parentObservation(parentObservation)
				.start();
		Set<String> tiles = geospatialBucketiser.bucketise(member);
		List<TileFragment> tileFragments = getTileFragments(parentFragment, tiles);
		Stream<LdesFragment> ldesFragments = addRelationsToCreatedFragments(parentFragment, tileFragments);
		ldesFragments
				.parallel()
				.forEach(ldesFragment -> super.addMemberToFragment(ldesFragment, member,
						geospatialFragmentationSpan));
		geospatialFragmentationSpan.stop();
	}

	private Stream<LdesFragment> addRelationsToCreatedFragments(LdesFragment parentFragment,
			List<TileFragment> tileFragments) {
		if (hasCreatedTiles(tileFragments)) {
			LdesFragment rootTileFragment = getRootTileFragment(parentFragment);
			return tileFragmentRelationsAttributer.addRelationsFromRootToBottom(rootTileFragment, tileFragments);
		} else {
			return tileFragments
					.stream()
					.map(TileFragment::ldesFragment);
		}
	}

	private boolean hasCreatedTiles(List<TileFragment> tileFragments) {
		return tileFragments
				.stream()
				.anyMatch(TileFragment::created);
	}

	private LdesFragment getRootTileFragment(LdesFragment parentFragment) {
		LdesFragment tileRootFragment = fragmentCreator.getOrCreateGeospatialFragment(parentFragment,
				FRAGMENT_KEY_TILE_ROOT).ldesFragment();
		super.addRelationFromParentToChild(parentFragment, tileRootFragment);
		return tileRootFragment;
	}

	private List<TileFragment> getTileFragments(LdesFragment parentFragment,
			Set<String> tiles) {
		return tiles
				.stream()
				.parallel()
				.map(tile -> fragmentCreator.getOrCreateGeospatialFragment(parentFragment, tile))
				.toList();
	}
}
