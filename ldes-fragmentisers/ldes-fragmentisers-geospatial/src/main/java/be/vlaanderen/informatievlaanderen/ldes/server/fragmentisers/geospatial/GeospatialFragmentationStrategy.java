package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.treenoderelations.TreeNodeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;

public class GeospatialFragmentationStrategy extends FragmentationStrategyDecorator {

    private final GeospatialBucketiser geospatialBucketiser;
    private final GeospatialFragmentCreator fragmentCreator;
    private final TileFragmentRelationsAttributer tileFragmentRelationsAttributer;
    private final Tracer tracer;
    private final ExecutorService executors;

    private LdesFragment rootTileFragment = null;

    public GeospatialFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
                                           LdesFragmentRepository ldesFragmentRepository,
                                           GeospatialBucketiser geospatialBucketiser, GeospatialFragmentCreator fragmentCreator,
                                           TileFragmentRelationsAttributer tileFragmentRelationsAttributer, Tracer tracer, TreeNodeRelationsRepository treeNodeRelationsRepository) {
        super(fragmentationStrategy, ldesFragmentRepository, treeNodeRelationsRepository);
        this.geospatialBucketiser = geospatialBucketiser;
        this.fragmentCreator = fragmentCreator;
        this.tileFragmentRelationsAttributer = tileFragmentRelationsAttributer;
        this.tracer = tracer;
        this.executors = Executors.newSingleThreadExecutor();
    }

    @Override
    public void addMemberToFragment(LdesFragment parentFragment, Member member, Span parentSpan) {
        Span geospatialFragmentationSpan = tracer.nextSpan(parentSpan).name("geospatial fragmentation").start();
        Set<String> tiles = geospatialBucketiser.bucketise(member);
        List<LdesFragment> tileFragments = getTileFragments(parentFragment, tiles);
        executors.submit(()->  addRelationsToCreatedFragments(parentFragment, tileFragments));
        tileFragments
                .stream()
                .parallel()
                .forEach(ldesFragment -> super.addMemberToFragment(ldesFragment, member,
                        geospatialFragmentationSpan));
        geospatialFragmentationSpan.end();
    }

    private void addRelationsToCreatedFragments(LdesFragment parentFragment,
                                                                List<LdesFragment> tileFragments) {

            LdesFragment chachedRootTile = getRootTileFragment(parentFragment);
            tileFragmentRelationsAttributer.addRelationsFromRootToBottom(chachedRootTile, tileFragments);
    }

    private LdesFragment getRootTileFragment(LdesFragment parentFragment) {
        if (rootTileFragment == null) {
            LdesFragment tileRootFragment = fragmentCreator.getOrCreateGeospatialFragment(parentFragment,
                    FRAGMENT_KEY_TILE_ROOT);
            super.addRelationFromParentToChild(parentFragment, tileRootFragment);
            rootTileFragment = tileRootFragment;
            return tileRootFragment;
        }
        return rootTileFragment;
    }

    private List<LdesFragment> getTileFragments(LdesFragment parentFragment,
                                                Set<String> tiles) {
        return tiles
                .stream()
                .parallel()
                .map(tile -> fragmentCreator.getOrCreateGeospatialFragment(parentFragment, tile))
                .toList();
    }
}
