package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.io.ParseException;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CoordinateToTileStringConverterTest {

    @ParameterizedTest(name = "Coordinate {0} at zoom level {1} is part of tile {2}")
    @ArgumentsSource(CoordinateZoomLevelArgumentsProvider.class)
    void test(Coordinate coordinate, int zoomLevel, String expectedTileString) {
        String actualTileString = CoordinateToTileStringConverter.convertCoordinate(coordinate, zoomLevel);
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

    @ParameterizedTest
    @ArgumentsSource(WktZoomTilesArgumentsProvider.class)
    void calculateTiles_byWktString(String wktString, int zoom, Set<String> expectedTiles) throws ParseException {
        Set<String> actual = CoordinateToTileStringConverter.calculateTiles(wktString, zoom);

        assertThat(actual).isEqualTo(expectedTiles);
    }

    static class WktZoomTilesArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            final int zoom = 11;

            return Stream.of(
                    Arguments.of(
                            "POINT(-4.1259489208459845 51.20651243817338)",
                            zoom,
                            Set.of("11/1000/683")
                    ),
                    Arguments.of(
                            "MULTIPOINT((-4.1219960153102875 51.20665065454949),(-3.4766042232513414 51.20342325695225))",
                            zoom,
                            Set.of("11/1000/683", "11/1004/683")
                    ),
                    Arguments.of(
                            "LINESTRING(-4.118006229400634 51.20099982929344,-4.123067557811736 51.099449367759576,-3.434469401836395 51.09963969871836,-3.451254665851592 51.20536257947589)",
                            zoom,
                            Set.of("11/1000/683", "11/1000/684", "11/1001/684", "11/1002/684", "11/1003/684", "11/1004/683", "11/1004/684")
                    ),
                    Arguments.of(
                            "MULTILINESTRING((-4.124218225479126 51.202786321257406,-3.4731951355934143 51.205261750161185),(-4.1903722286224365 51.01776053407323,-3.5318201780319214 51.023780510283046))",
                            zoom,
                            Set.of("11/1000/683", "11/1000/685", "11/1001/683", "11/1001/685", "11/1002/683", "11/1002/685", "11/1003/683", "11/1003/685", "11/1004/683")
                    ),
                    Arguments.of(
                            "POLYGON((-4.156482517719269 51.21848699100292,-4.156458377838135 51.106470882289756,-3.962151110172272 51.10533242179693,-3.971670269966125 51.21793426510715,-4.156482517719269 51.21848699100292))",
                            zoom,
                            Set.of("11/1001/684", "11/1001/683", "11/1000/684", "11/1000/683")
                    ),
                    Arguments.of(
                            "MULTIPOLYGON(((-4.124027788639068 51.19225291766125,-4.123448431491851 51.02445872093938,-3.8344457745552054 51.02112493327536,-3.8435277342796317 51.19463142177099,-4.124027788639068 51.19225291766125),(-4.18140023946762 51.212875422863135,-4.181075692176818 51.00798990696538,-3.780844509601592 51.004037196377624,-3.798305690288543 51.215456157106246,-4.18140023946762 51.212875422863135)))",
                            zoom,
                            Set.of("11/1000/683", "11/1000/684", "11/1000/685", "11/1001/683", "11/1001/685", "11/1002/683", "11/1002/684", "11/1002/685")
                    )
            );
        }
    }
}