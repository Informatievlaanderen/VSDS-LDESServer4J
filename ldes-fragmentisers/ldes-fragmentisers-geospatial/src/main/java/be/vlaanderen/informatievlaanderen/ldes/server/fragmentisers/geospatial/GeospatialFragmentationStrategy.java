package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.TileFragment;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;

public class GeospatialFragmentationStrategy extends FragmentationStrategyDecorator {

	private final GeospatialBucketiser geospatialBucketiser;
	private final GeospatialFragmentCreator fragmentCreator;
	private final TileFragmentRelationsAttributer tileFragmentRelationsAttributer;
	private final Tracer tracer;

	public GeospatialFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
			LdesFragmentRepository ldesFragmentRepository,
			GeospatialBucketiser geospatialBucketiser, GeospatialFragmentCreator fragmentCreator,
			TileFragmentRelationsAttributer tileFragmentRelationsAttributer, Tracer tracer) {
		super(fragmentationStrategy, ldesFragmentRepository);
		this.geospatialBucketiser = geospatialBucketiser;
		this.fragmentCreator = fragmentCreator;
		this.tileFragmentRelationsAttributer = tileFragmentRelationsAttributer;
		this.tracer = tracer;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, LdesMember ldesMember, Span parentSpan) {
		Span geospatialFragmentationSpan = tracer.nextSpan(parentSpan).name("geospatial fragmentation").start();
		Set<String> tiles = geospatialBucketiser.bucketise(ldesMember);
		List<TileFragment> tileFragments = getTileFragments(parentFragment, tiles);
		Stream<LdesFragment> ldesFragments = addRelationsToCreatedFragments(parentFragment, tileFragments);
		ldesFragments
				.parallel()
				.forEach(ldesFragment -> super.addMemberToFragment(ldesFragment, ldesMember,
						geospatialFragmentationSpan));
		geospatialFragmentationSpan.end();
	}

	private Stream<LdesFragment> addRelationsToCreatedFragments(LdesFragment parentFragment,
			List<TileFragment> tileFragments) {
		if (hasCreatedTiles(tileFragments)) {
			LdesFragment rootTileFragment = getRootTileFragment(parentFragment);
			return tileFragmentRelationsAttributer.addRelationsFromRootToBottom(rootTileFragment, tileFragments);
		} else {
			return tileFragments
					.parallelStream() // TODO: is parallelisation worth the effort here? TileFragment::ldesFragment
										// seems to be quite lightweight.
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
