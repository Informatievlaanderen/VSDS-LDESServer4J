package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.hierarchy.bucketising;

import org.locationtech.jts.geom.Coordinate;

public interface CoordinateConverter {

	Coordinate convertCoordinate(final Coordinate coordinate);
}
