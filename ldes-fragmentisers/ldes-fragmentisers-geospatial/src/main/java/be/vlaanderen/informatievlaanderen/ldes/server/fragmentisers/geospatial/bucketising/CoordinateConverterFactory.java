package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.Projections.LAMBERT72;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.Projections.NOOP;

public class CoordinateConverterFactory {
	private CoordinateConverterFactory() {
	}

	public static CoordinateConverter getCoordinateConverter(String projection) {
		if (LAMBERT72.name().equalsIgnoreCase(projection)) {
			return new Lambert72CoordinateConverter();
		}
		if (NOOP.name().equalsIgnoreCase(projection)) {
			return new NoopCoordinateConverter();
		}
		throw new IllegalArgumentException("No coordinateConverter for projection: " + projection);
	}
}
