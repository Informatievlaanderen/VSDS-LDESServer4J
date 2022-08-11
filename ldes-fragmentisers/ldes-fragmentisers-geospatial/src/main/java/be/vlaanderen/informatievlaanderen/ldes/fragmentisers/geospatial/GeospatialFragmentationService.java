package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.connected.ConnectedFragmentsFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

public class GeospatialFragmentationService implements FragmentationService {

    private final LdesConfig ldesConfig;
    private final LdesMemberRepository ldesMemberRepository;
    private final LdesFragmentRepository ldesFragmentRepository;
    private final FragmentCreator fragmentCreator;
    private final GeospatialBucketiser geospatialBucketiser;
    private final ConnectedFragmentsFinder connectedFragmentsFinder;

    public GeospatialFragmentationService(LdesConfig ldesConfig, LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository, FragmentCreator fragmentCreator, GeospatialBucketiser geospatialBucketiser, ConnectedFragmentsFinder connectedFragmentsFinder) {
        this.ldesConfig = ldesConfig;
        this.ldesMemberRepository = ldesMemberRepository;
        this.ldesFragmentRepository = ldesFragmentRepository;
        this.fragmentCreator = fragmentCreator;
        this.geospatialBucketiser = geospatialBucketiser;
        this.connectedFragmentsFinder = connectedFragmentsFinder;
    }

    @Override
    public void addMemberToFragment(String ldesMemberId) {
        LdesMember ldesMember = ldesMemberRepository.getLdesMemberById(ldesMemberId).orElseThrow(() -> new MemberNotFoundException(ldesMemberId));
        Set<String> tiles = geospatialBucketiser.bucketise(ldesMember);
        List<LdesFragment> ldesFragments = retrieveFragmentsOrCreateNewFragments(tiles);
        ldesFragments.forEach(ldesFragment -> {
            ldesFragment.addMember(ldesMemberId);
            List<LdesFragment> connectedFragments = connectedFragmentsFinder.findConnectedFragments(ldesFragment);
            connectedFragments.forEach(ldesFragmentRepository::saveFragment);
        });
    }

    private List<LdesFragment> retrieveFragmentsOrCreateNewFragments(Set<String> tiles) {
        return tiles
                .stream().map(tile -> {
                    Optional<LdesFragment> ldesFragment = ldesFragmentRepository.retrieveFragment(new LdesFragmentRequest(ldesConfig.getCollectionName(), List.of(new FragmentPair(FRAGMENT_KEY_TILE, tile))));
                    return ldesFragment.orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(), new FragmentPair(FRAGMENT_KEY_TILE, tile)));
                })
                .toList();
    }
}
