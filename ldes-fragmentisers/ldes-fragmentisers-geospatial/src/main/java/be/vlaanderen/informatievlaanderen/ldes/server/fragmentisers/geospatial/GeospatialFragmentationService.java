package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationServiceDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.GeospatialRelationsAttributer;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;

public class GeospatialFragmentationService extends FragmentationServiceDecorator {

	private final LdesMemberRepository ldesMemberRepository;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final FragmentCreator fragmentCreator;
	private final GeospatialBucketiser geospatialBucketiser;
	private final Tracer tracer;
	private Span rootSpan;

	public GeospatialFragmentationService(FragmentationService fragmentationService,
			LdesMemberRepository ldesMemberRepository,
			LdesFragmentRepository ldesFragmentRepository, FragmentCreator fragmentCreator,
			GeospatialBucketiser geospatialBucketiser, Tracer tracer) {
		super(fragmentationService, ldesFragmentRepository);
		this.ldesMemberRepository = ldesMemberRepository;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.fragmentCreator = fragmentCreator;
		this.geospatialBucketiser = geospatialBucketiser;
		this.tracer = tracer;
	}

	@Override
	public void addMemberToFragment(LdesFragment parentFragment, String ldesMemberId) {
		rootSpan = this.tracer.nextSpan().name("Geobased fragmentation").start();

		LdesMember ldesMember = ldesMemberRepository.getLdesMemberById(ldesMemberId)
				.orElseThrow(() -> new MemberNotFoundException(ldesMemberId));
		Set<String> tiles = geospatialBucketiser.bucketise(ldesMember);
		rootSpan.event("buckets prepared");
		List<LdesFragment> ldesFragments = retrieveFragmentsOrCreateNewFragments(parentFragment.getFragmentInfo(),
				tiles);
		rootSpan.event("fragment created");
		addRelationsToRootFragment(parentFragment, ldesFragments);
		rootSpan.end();
		ldesFragments.forEach(ldesFragment -> super.addMemberToFragment(ldesFragment, ldesMemberId));
	}

	private void addRelationsToRootFragment(LdesFragment parentFragment, List<LdesFragment> ldesFragments) {
		List<FragmentPair> fragmentPairs = new ArrayList<>(parentFragment.getFragmentInfo().getFragmentPairs());
		fragmentPairs.add(new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT));
		FragmentInfo fragmentInfo = new FragmentInfo(
				parentFragment.getFragmentInfo().getViewName(), fragmentPairs);
		LdesFragment rootFragment = ldesFragmentRepository
				.retrieveFragment(new LdesFragmentRequest(
						parentFragment.getFragmentInfo().getViewName(),
						List.of(new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT))))
				.orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(), fragmentInfo));
		rootSpan.event("retrieved root fragment");

		super.addRelationFromParentToChild(parentFragment, rootFragment);
		rootSpan.event("add relation from parent to fragmenter root");

		GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();
		ldesFragments.forEach(
				ldesFragment -> relationsAttributer.addRelationToParentFragment(rootFragment, ldesFragment));
		ldesFragmentRepository.saveFragment(rootFragment);
		ldesFragments.forEach(ldesFragmentRepository::saveFragment);
		rootSpan.event("add relation from root to individual fragments");
	}

	private List<LdesFragment> retrieveFragmentsOrCreateNewFragments(FragmentInfo fragmentInfo,
			Set<String> tiles) {
		return tiles
				.stream().map(tile -> {
					List<FragmentPair> fragmentPairs = new ArrayList<>(fragmentInfo.getFragmentPairs());
					fragmentPairs.add(new FragmentPair(FRAGMENT_KEY_TILE, tile));
					Optional<LdesFragment> ldesFragment = ldesFragmentRepository
							.retrieveFragment(new LdesFragmentRequest(fragmentInfo.getViewName(),
									fragmentPairs));
					FragmentInfo fragmentInfo1 = new FragmentInfo(
							fragmentInfo.getViewName(), fragmentPairs);
					return ldesFragment.orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(),
							fragmentInfo1));
				})
				.toList();
	}
}
