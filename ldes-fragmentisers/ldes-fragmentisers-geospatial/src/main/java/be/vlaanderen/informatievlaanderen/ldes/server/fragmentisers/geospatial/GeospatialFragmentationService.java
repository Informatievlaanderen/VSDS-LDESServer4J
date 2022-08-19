package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.BoundingBox;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.ConnectedFragmentsFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.BoundingBoxConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.TileConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.*;

public class GeospatialFragmentationService implements FragmentationService {

    private final LdesConfig ldesConfig;
    private final LdesMemberRepository ldesMemberRepository;
    private final LdesFragmentRepository ldesFragmentRepository;
    private final FragmentCreator fragmentCreator;
    private final GeospatialBucketiser geospatialBucketiser;

    public GeospatialFragmentationService(LdesConfig ldesConfig, LdesMemberRepository ldesMemberRepository,
                                          LdesFragmentRepository ldesFragmentRepository, FragmentCreator fragmentCreator,
                                          GeospatialBucketiser geospatialBucketiser) {
        this.ldesConfig = ldesConfig;
        this.ldesMemberRepository = ldesMemberRepository;
        this.ldesFragmentRepository = ldesFragmentRepository;
        this.fragmentCreator = fragmentCreator;
        this.geospatialBucketiser = geospatialBucketiser;
    }

    @Override
    public void addMemberToFragment(String ldesMemberId) {
        LdesMember ldesMember = ldesMemberRepository.getLdesMemberById(ldesMemberId)
                .orElseThrow(() -> new MemberNotFoundException(ldesMemberId));
        Set<String> tiles = geospatialBucketiser.bucketise(ldesMember);
        List<LdesFragment> ldesFragments = retrieveFragmentsOrCreateNewFragments(tiles);
        ldesFragments.forEach(ldesFragment -> {
            ldesFragment.addMember(ldesMemberId);
            ldesFragmentRepository.saveFragment(ldesFragment);
        });
		addRelationsToRootFragment(ldesFragments);
    }

	private void addRelationsToRootFragment(List<LdesFragment> ldesFragments) {
        LdesFragment rootFragment = ldesFragmentRepository
                .retrieveFragment(new LdesFragmentRequest(ldesConfig.getCollectionName(),
                        List.of(new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT))))
                .orElseGet(() ->fragmentCreator.createNewFragment(Optional.empty(),
                        new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT)));



        ldesFragments.forEach(ldesFragment -> {
            String targetWKT = getWKT(ldesFragment);

            TreeRelation relationToTargetFragment = new TreeRelation(GEOSPARQL_AS_WKT, ldesFragment.getFragmentId(),
                    WGS_84 + " " + targetWKT, WKT_DATA_TYPE, TREE_GEOSPATIALLY_CONTAINS_RELATION);
            rootFragment.addRelation(relationToTargetFragment);
        });
        ldesFragmentRepository.saveFragment(rootFragment);
	}

	private List<LdesFragment> retrieveFragmentsOrCreateNewFragments(Set<String> tiles) {
        return tiles
                .stream().map(tile -> {
                    Optional<LdesFragment> ldesFragment = ldesFragmentRepository
                            .retrieveFragment(new LdesFragmentRequest(ldesConfig.getCollectionName(),
                                    List.of(new FragmentPair(FRAGMENT_KEY_TILE, tile))));
                    return ldesFragment.orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(),
                            new FragmentPair(FRAGMENT_KEY_TILE, tile)));
                })
                .toList();
    }

    private String getWKT(LdesFragment currentFragment) {
        Tile currentTile = TileConverter.fromString(currentFragment.getFragmentInfo().getValue());
        BoundingBox currentBoundingBox = new BoundingBox(currentTile);
        return BoundingBoxConverter.toWKT(currentBoundingBox);
    }
}
