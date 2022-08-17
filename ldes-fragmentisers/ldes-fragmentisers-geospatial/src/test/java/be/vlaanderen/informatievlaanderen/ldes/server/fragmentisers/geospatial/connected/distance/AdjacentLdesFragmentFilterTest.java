package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.distance;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AdjacentLdesFragmentFilterTest {

	LdesFragment ldesFragment = new LdesFragment("",
			new FragmentInfo("", List.of(new FragmentPair(GeospatialConstants.FRAGMENT_KEY_TILE, "15/4/4"))));

	AdjacentLdesFragmentFilter adjacentLdesFragmentFilter = new AdjacentLdesFragmentFilter(ldesFragment);

	@ParameterizedTest(name = "Fragment {0} is a real neighbour: {1}")
	@ArgumentsSource(LdesFragmentArgumentsSource.class)
	void when_DistanceBetweenTwoFragmentsIsOne_FragmentsAreNeighbours(LdesFragment potentialNeighbourFragment,
			boolean isNeighbour) {
		assertEquals(isNeighbour, adjacentLdesFragmentFilter.test(potentialNeighbourFragment));
	}

	static class LdesFragmentArgumentsSource implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(getLdesFragment("15/4/4"), false),
					Arguments.of(getLdesFragment("15/4/3"), true),
					Arguments.of(getLdesFragment("15/3/4"), true),
					Arguments.of(getLdesFragment("15/4/5"), true),
					Arguments.of(getLdesFragment("15/5/4"), true),
					Arguments.of(getLdesFragment("15/3/3"), false),
					Arguments.of(getLdesFragment("15/3/5"), false),
					Arguments.of(getLdesFragment("15/5/3"), false),
					Arguments.of(getLdesFragment("15/5/5"), false),
					Arguments.of(getLdesFragment("15/9/10"), false),
					Arguments.of(getLdesFragment("15/0/0"), false));
		}

		private LdesFragment getLdesFragment(String fragmentValue) {
			return new LdesFragment("", new FragmentInfo("",
					List.of(new FragmentPair(GeospatialConstants.FRAGMENT_KEY_TILE, fragmentValue))));
		}
	}

}