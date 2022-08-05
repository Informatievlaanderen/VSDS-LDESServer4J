package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.bucketising.CoordinateConverter;
import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.bucketising.Lambert72CoordinateConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Lambert72CoordinateConverterTest {
    CoordinateConverter coordinateConverter = new Lambert72CoordinateConverter();

    @Test
    @DisplayName("Verify conversion of Lambert72 coordinates")
    void when_Lambert72CoordinateIsGiven_ConversionReturnsWGS84Coordinate() {
        Coordinate coordinate = new Coordinate(146955.9159332997, 163274.52348441593);
        Coordinate convertedCoordinate = coordinateConverter.convertCoordinate(coordinate);
        assertEquals(4.325601366634673, convertedCoordinate.x);
        assertEquals(50.77993794535044, convertedCoordinate.y);
    }

}