package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.GeospatialRelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ConnectedFragmentsFinder {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final GeospatialRelationsAttributer relationsAttributer;
	private final FragmentGenerator fragmentGenerator;

	public ConnectedFragmentsFinder(LdesFragmentRepository ldesFragmentRepository,
			GeospatialRelationsAttributer relationsAttributer, FragmentGenerator fragmentGenerator) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.fragmentGenerator = fragmentGenerator;
		this.relationsAttributer = relationsAttributer;
	}

	public List<LdesFragment> findConnectedFragments(LdesFragment ldesFragment) {
		List<LdesFragment> availableFragments = ldesFragmentRepository.retrieveAllFragments()
				.stream().filter(ldesFragment1 -> getSetOfFragmentKeys(ldesFragment1).equals(getSetOfFragmentKeys(ldesFragment)))
				.toList();
		if (availableFragments.isEmpty()) {
			return List.of(ldesFragment);
		} else {
			return findConnectedFragments(ldesFragment, availableFragments);
		}
	}

	private Set<String> getSetOfFragmentKeys(LdesFragment ldesFragment1) {
		return ldesFragment1.getFragmentInfo().getFragmentPairs().stream().map(FragmentPair::fragmentKey).collect(Collectors.toSet());
	}

	private List<LdesFragment> findConnectedFragments(LdesFragment ldesFragment,
			List<LdesFragment> availableFragments) {
		List<LdesFragment> ldesFragments = fragmentGenerator.generateFragmentPathToClosestFragment(ldesFragment,
				availableFragments);
		for (int i = 0; i < ldesFragments.size(); i++) {
			LdesFragment currentFragment = ldesFragments.get(i);
			relationsAttributer.addGeospatialRelationsToNeighbouringFragments(currentFragment, ldesFragments);
		}
		return ldesFragments;
	}

}
