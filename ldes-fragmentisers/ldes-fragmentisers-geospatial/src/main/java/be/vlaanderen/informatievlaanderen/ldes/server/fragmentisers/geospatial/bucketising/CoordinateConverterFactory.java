package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialProperties.NOOP;

public class CoordinateConverterFactory {
	private CoordinateConverterFactory() {
	}

	public static CoordinateConverter getCoordinateConverter(String projection) {
		if ("lambert72".equalsIgnoreCase(projection)) {
			return new Lambert72CoordinateConverter();
		}
		if (NOOP.equalsIgnoreCase(projection)) {
			return new NoopCoordinateConverter();
		}
		throw new IllegalArgumentException("No coordinateConverter for projection: " + projection);
	}
}
