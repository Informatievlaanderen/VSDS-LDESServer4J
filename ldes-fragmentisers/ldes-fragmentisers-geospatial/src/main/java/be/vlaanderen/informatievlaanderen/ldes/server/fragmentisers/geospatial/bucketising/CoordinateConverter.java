package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import org.locationtech.jts.geom.Coordinate;

public interface CoordinateConverter {

    Coordinate convertCoordinate(final Coordinate coordinate);
}
