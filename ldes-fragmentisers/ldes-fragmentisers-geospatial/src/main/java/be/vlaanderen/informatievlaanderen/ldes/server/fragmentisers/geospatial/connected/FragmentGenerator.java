package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class FragmentGenerator {

    private final ClosestFragmentDiscoverer closestFragmentDiscoverer;
    private final FragmentPathCreator fragmentPathCreator;

    public FragmentGenerator(ClosestFragmentDiscoverer closestFragmentDiscoverer, FragmentPathCreator fragmentPathCreator) {
        this.closestFragmentDiscoverer = closestFragmentDiscoverer;
        this.fragmentPathCreator = fragmentPathCreator;
    }

    public List<LdesFragment> generateFragmentPathToClosestFragment(LdesFragment ldesFragment, List<LdesFragment> availableFragments) {
        List<LdesFragment> ldesFragments = new ArrayList<>();
        ldesFragments.add(ldesFragment);
        LdesFragment closestFragment = closestFragmentDiscoverer.getClosestFragment(ldesFragment, availableFragments);
        ldesFragments.add(closestFragment);
        Set<LdesFragment> inBetweenFragments = fragmentPathCreator.createFragmentPath(ldesFragment, closestFragment);
        ldesFragments.addAll(inBetweenFragments);
        return ldesFragments;
    }
}
