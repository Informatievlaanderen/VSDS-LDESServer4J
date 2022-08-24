package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationServiceDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.ConnectedFragmentsFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

public class GeospatialFragmentationService extends FragmentationServiceDecorator {

	private final LdesConfig ldesConfig;
	private final LdesMemberRepository ldesMemberRepository;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final FragmentCreator fragmentCreator;
	private final GeospatialBucketiser geospatialBucketiser;
	private final ConnectedFragmentsFinder connectedFragmentsFinder;

	public GeospatialFragmentationService(FragmentationService fragmentationService, LdesConfig ldesConfig, LdesMemberRepository ldesMemberRepository,
			LdesFragmentRepository ldesFragmentRepository, FragmentCreator fragmentCreator,
			GeospatialBucketiser geospatialBucketiser, ConnectedFragmentsFinder connectedFragmentsFinder) {
		super(fragmentationService);
		this.ldesConfig = ldesConfig;
		this.ldesMemberRepository = ldesMemberRepository;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.fragmentCreator = fragmentCreator;
		this.geospatialBucketiser = geospatialBucketiser;
		this.connectedFragmentsFinder = connectedFragmentsFinder;
	}

	@Override
	public void addMemberToFragment(List<FragmentPair> fragmentPairList, String ldesMemberId) {
		LdesMember ldesMember = ldesMemberRepository.getLdesMemberById(ldesMemberId)
				.orElseThrow(() -> new MemberNotFoundException(ldesMemberId));
		Set<String> tiles = geospatialBucketiser.bucketise(ldesMember);
		List<LdesFragment> ldesFragments = retrieveFragmentsOrCreateNewFragments(fragmentPairList,tiles);
		ldesFragments.forEach(ldesFragment -> {
			List<LdesFragment> connectedFragments = connectedFragmentsFinder.findConnectedFragments(ldesFragment);
			connectedFragments.forEach(ldesFragmentRepository::saveFragment);

			super.addMemberToFragment(ldesFragment.getFragmentInfo().getFragmentPairs(), ldesMemberId);

		});
	}

	private List<LdesFragment> retrieveFragmentsOrCreateNewFragments(List<FragmentPair> fragmentPairList, Set<String> tiles) {

		return tiles
				.stream().map(tile -> {
					List<FragmentPair> fragmentPairs = new ArrayList<>(fragmentPairList.stream().toList());
					fragmentPairs.add(new FragmentPair(FRAGMENT_KEY_TILE, tile));
					Optional<LdesFragment> ldesFragment = ldesFragmentRepository
							.retrieveFragment(new LdesFragmentRequest(ldesConfig.getCollectionName(),
									fragmentPairs));
					return ldesFragment.orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(),
							fragmentPairs));
				})
				.toList();
	}
}
