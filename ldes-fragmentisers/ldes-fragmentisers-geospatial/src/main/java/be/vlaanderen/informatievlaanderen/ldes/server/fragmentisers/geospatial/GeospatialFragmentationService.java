package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationServiceDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.GeospatialRelationsAttributer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;

public class GeospatialFragmentationService extends FragmentationServiceDecorator {

	private final LdesConfig ldesConfig;
	private final LdesMemberRepository ldesMemberRepository;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final FragmentCreator fragmentCreator;
	private final GeospatialBucketiser geospatialBucketiser;

	public GeospatialFragmentationService(FragmentationService fragmentationService, LdesConfig ldesConfig,
			LdesMemberRepository ldesMemberRepository,
			LdesFragmentRepository ldesFragmentRepository, FragmentCreator fragmentCreator,
			GeospatialBucketiser geospatialBucketiser) {
		super(fragmentationService, ldesFragmentRepository, ldesConfig);
		this.ldesConfig = ldesConfig;
		this.ldesMemberRepository = ldesMemberRepository;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.fragmentCreator = fragmentCreator;
		this.geospatialBucketiser = geospatialBucketiser;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, String ldesMemberId) {
		List<FragmentPair> parentFragmentPairs = parentFragment.getFragmentInfo().getFragmentPairs();
		LdesMember ldesMember = ldesMemberRepository.getLdesMemberById(ldesMemberId)
				.orElseThrow(() -> new MemberNotFoundException(ldesMemberId));
		Set<String> tiles = geospatialBucketiser.bucketise(ldesMember);
		List<LdesFragment> ldesFragments = retrieveFragmentsOrCreateNewFragments(parentFragmentPairs, tiles);
		addRelationsToRootFragment(parentFragment, ldesFragments);
		ldesFragments
				.forEach(ldesFragment -> super.addMemberToFragment(ldesFragment, ldesMemberId));
	}

	private void addRelationsToRootFragment(LdesFragment parentFragment, List<LdesFragment> ldesFragments) {
		LdesFragment rootFragment = ldesFragmentRepository
				.retrieveFragment(new LdesFragmentRequest(ldesConfig.getCollectionName(),
						List.of(new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT))))
				.orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(),
						List.of(new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT))));

		GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();
		TreeRelation treeRelation = new TreeRelation("", rootFragment.getFragmentId(), "", "", "");
		if (!parentFragment.getRelations().contains(treeRelation)) {
			parentFragment.addRelation(treeRelation);
			ldesFragmentRepository.saveFragment(parentFragment);
		}

		ldesFragments.forEach(
				ldesFragment -> relationsAttributer.addRelationToParentFragment(rootFragment, ldesFragment));
		ldesFragmentRepository.saveFragment(rootFragment);
		ldesFragments.forEach(ldesFragmentRepository::saveFragment);
	}

	private LdesFragment retrieveParentFragment(List<FragmentPair> fragmentPairList) {
		return ldesFragmentRepository
				.retrieveFragment(new LdesFragmentRequest(ldesConfig.getCollectionName(), fragmentPairList))
				.orElseThrow(RuntimeException::new);
	}

	private List<LdesFragment> retrieveFragmentsOrCreateNewFragments(List<FragmentPair> fragmentPairList,
			Set<String> tiles) {

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
