package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.GeospatialRelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.TileConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConnectedFragmentsFinder {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final GeospatialRelationsAttributer relationsAttributer;
	private final FragmentGenerator fragmentGenerator;

	private final GeospatialConfig geospatialConfig;

	public ConnectedFragmentsFinder(LdesFragmentRepository ldesFragmentRepository,
			GeospatialRelationsAttributer relationsAttributer, FragmentGenerator fragmentGenerator,
			GeospatialConfig geospatialConfig) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.fragmentGenerator = fragmentGenerator;
		this.relationsAttributer = relationsAttributer;
		this.geospatialConfig = geospatialConfig;
	}

	public List<LdesFragment> findConnectedFragments(LdesFragment ldesFragment, List<FragmentPair> fragmentPairList) {
		List<LdesFragment> availableFragments = ldesFragmentRepository.retrieveAllFragments()
				.stream()
				.filter(ldesFragment1 -> ldesFragment1.getFragmentInfo().getFragmentPairs().stream().anyMatch(
						fragmentPair -> fragmentPair.fragmentKey().equals(GeospatialConstants.FRAGMENT_KEY_TILE)))
				.filter(ldesFragment1 -> TileConverter.fromString(ldesFragment1.getFragmentInfo().getFragmentPairs()
						.stream()
						.filter(fragmentPair -> fragmentPair.fragmentKey()
								.equals(GeospatialConstants.FRAGMENT_KEY_TILE))
						.findFirst().get().fragmentValue()).getZoom() == geospatialConfig.getMaxZoomLevel())
				.toList();
		if (availableFragments.isEmpty()) {
			return List.of(ldesFragment);
		} else {
			return findConnectedFragments(ldesFragment, availableFragments, fragmentPairList);
		}
	}

	private List<LdesFragment> findConnectedFragments(LdesFragment ldesFragment,
			List<LdesFragment> availableFragments, List<FragmentPair> fragmentPairList) {
		List<LdesFragment> ldesFragments = fragmentGenerator.generateFragmentPathToClosestFragment(ldesFragment,
				availableFragments, fragmentPairList);
		for (int i = 0; i < ldesFragments.size(); i++) {
			LdesFragment currentFragment = ldesFragments.get(i);
			relationsAttributer.addGeospatialRelationsToNeighbouringFragments(currentFragment, ldesFragments);
		}
		return ldesFragments;
	}

}
