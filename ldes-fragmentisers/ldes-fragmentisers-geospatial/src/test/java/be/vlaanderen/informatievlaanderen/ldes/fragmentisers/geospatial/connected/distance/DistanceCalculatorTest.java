package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.connected.distance;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistanceCalculatorTest {

    @ParameterizedTest(name = "Distance between ({0},{1}) and ({2},{3}) is equal to {4}")
    @ArgumentsSource(LocationsDistanceArgumentsProvider.class)
    void when_FourIntsAreGiven_DistanceBetweenTwoLocationsIsCalculated(int x1, int y1, int x2, int y2, double expectedDistance) {
        double actualDistance = DistanceCalculator.calculateDistance(x1, y1, x2, y2);
        assertEquals(expectedDistance, actualDistance);
    }

    static class LocationsDistanceArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(0, 0, 1, 1, Math.sqrt(2)),
                    Arguments.of(0, 0, 0, -1, 1),
                    Arguments.of(0, 0, -1, 0, 1),
                    Arguments.of(0, 0, 1, 0, 1),
                    Arguments.of(0, 0, 0, 1, 1)
            );
        }
    }

}