package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.RootFragmentService;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.GeospatialRelationsAttributer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

public class GeospatialFragmentationService implements FragmentationService {

	private final LdesConfig ldesConfig;
	private final LdesMemberRepository ldesMemberRepository;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final FragmentCreator fragmentCreator;
	private final GeospatialBucketiser geospatialBucketiser;

	private final RootFragmentService rootFragmentService;

	public GeospatialFragmentationService(LdesConfig ldesConfig, LdesMemberRepository ldesMemberRepository,
			LdesFragmentRepository ldesFragmentRepository, FragmentCreator fragmentCreator,
			GeospatialBucketiser geospatialBucketiser, RootFragmentService rootFragmentService) {
		this.ldesConfig = ldesConfig;
		this.ldesMemberRepository = ldesMemberRepository;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.fragmentCreator = fragmentCreator;
		this.geospatialBucketiser = geospatialBucketiser;
		this.rootFragmentService = rootFragmentService;
	}

	@Override
	public List<LdesFragment> addMemberToFragment(List<LdesFragment> parentFragments, String ldesMemberId) {
		List<LdesFragment> modifiedLdesFragments;

		if (parentFragments.isEmpty()) {
			modifiedLdesFragments = fragmentMember(rootFragmentService.getRootFragment(), ldesMemberId);
		} else {
			modifiedLdesFragments = parentFragments.stream()
					.filter(ldesFragment -> !ldesFragment.getMemberIds().isEmpty())
					.flatMap(ldesFragment -> ldesFragment.getMemberIds().stream()
							.flatMap(member -> fragmentMember(ldesFragment, member).stream()))
					.toList();
		}
		modifiedLdesFragments.forEach(ldesFragmentRepository::saveFragment);
		return modifiedLdesFragments;
	}

	private List<LdesFragment> fragmentMember(LdesFragment parentFragment, String ldesMemberId) {
		List<LdesFragment> modifiedFragments = setupTileFragments(ldesMemberId);

		modifiedFragments.forEach(ldesFragment -> {
			ldesFragment.addMember(ldesMemberId);
		});
		rootFragmentService.addRelationToParentFragment(parentFragment, modifiedFragments);

		return modifiedFragments;
	}

	private List<LdesFragment> setupTileFragments(String ldesMemberId) {
		LdesMember ldesMember = ldesMemberRepository.getLdesMemberById(ldesMemberId)
				.orElseThrow(() -> new MemberNotFoundException(ldesMemberId));
		Set<String> tiles = geospatialBucketiser.bucketise(ldesMember);
		return retrieveFragmentsOrCreateNewFragments(tiles);
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
}
