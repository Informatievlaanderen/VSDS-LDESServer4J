package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import org.locationtech.jts.geom.Coordinate;

public class NoopCoordinateConverter implements CoordinateConverter {
	@Override
	public Coordinate convertCoordinate(Coordinate coordinate) {
		return coordinate;
	}
}
