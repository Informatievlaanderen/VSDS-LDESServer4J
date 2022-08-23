package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.locationtech.jts.geom.Coordinate;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoordinateToTileStringConverterTest {

	CoordinateToTileStringConverter converter = new CoordinateToTileStringConverter();

	@ParameterizedTest(name = "Coordinate {0} at zoom level {1} is part of tile {2}")
	@ArgumentsSource(CoordinateZoomLevelArgumentsProvider.class)
	void test(Coordinate coordinate, int zoomLevel, String expectedTileString) {
		String actualTileString = converter.convertCoordinate(coordinate, zoomLevel);
		assertEquals(expectedTileString, actualTileString);
	}

	static class CoordinateZoomLevelArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(new Coordinate(4.325601366634673, 50.77993794535044), 15, "15/16777/11001"),
					Arguments.of(new Coordinate(4.325601366634673, 50.77993794535044), 16, "16/33555/22003"),
					Arguments.of(new Coordinate(4.325601366634673, 50.77993794535044), 17, "17/67110/44006"),
					Arguments.of(new Coordinate(4.325601366634673, 50.77993794535044), 18, "18/134221/88013"),
					Arguments.of(new Coordinate(4.325601366634673, 50.77993794535044), 19, "19/268443/176027"));
		}
	}

}