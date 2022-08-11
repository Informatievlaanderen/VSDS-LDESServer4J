package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.bucketising;

import org.locationtech.jts.geom.Coordinate;

public interface CoordinateConverter {

    Coordinate convertCoordinate(final Coordinate coordinate);
}
