package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class FragmentGenerator {

	private final ClosestFragmentDiscoverer closestFragmentDiscoverer;
	private final FragmentPathCreator fragmentPathCreator;

	public FragmentGenerator(ClosestFragmentDiscoverer closestFragmentDiscoverer,
			FragmentPathCreator fragmentPathCreator) {
		this.closestFragmentDiscoverer = closestFragmentDiscoverer;
		this.fragmentPathCreator = fragmentPathCreator;
	}

	public List<LdesFragment> generateFragmentPathToClosestFragment(LdesFragment ldesFragment,
																	List<LdesFragment> availableFragments, List<FragmentPair> fragmentPairList) {
		List<LdesFragment> ldesFragments = new ArrayList<>();
		ldesFragments.add(ldesFragment);
		LdesFragment closestFragment = closestFragmentDiscoverer.getClosestFragment(ldesFragment, availableFragments);
		ldesFragments.add(closestFragment);
		Set<LdesFragment> inBetweenFragments = fragmentPathCreator.createFragmentPath(ldesFragment, closestFragment, fragmentPairList);
		inBetweenFragments.forEach(fragment->{
			if(!ldesFragments.stream().map(LdesFragment::getFragmentId).toList().contains(fragment.getFragmentId())){
				ldesFragments.add(fragment);
			}
		});
		return ldesFragments;
	}
}
