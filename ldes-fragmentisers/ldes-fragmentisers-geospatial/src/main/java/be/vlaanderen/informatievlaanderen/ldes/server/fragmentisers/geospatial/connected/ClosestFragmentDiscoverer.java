package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.distance.LdesFragmentDistanceComparator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.exceptions.NoClosestFragmentFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClosestFragmentDiscoverer {

	public LdesFragment getClosestFragment(LdesFragment ldesFragment, List<LdesFragment> availableFragments) {
		return availableFragments
				.stream()
				.min(new LdesFragmentDistanceComparator(ldesFragment))
				.orElseThrow(() -> new NoClosestFragmentFoundException(ldesFragment, availableFragments));
	}
}
