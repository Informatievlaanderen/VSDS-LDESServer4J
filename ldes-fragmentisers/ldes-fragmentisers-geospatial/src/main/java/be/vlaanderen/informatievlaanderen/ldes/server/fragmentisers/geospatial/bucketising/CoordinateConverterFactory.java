package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

public class CoordinateConverterFactory {
	private CoordinateConverterFactory() {
	}

	public static CoordinateConverter getCoordinateConverter(String projection) {
		if ("lambert72".equalsIgnoreCase(projection)) {
			return new Lambert72CoordinateConverter();
		}
		throw new IllegalArgumentException("No coordinateConverter for projection: " + projection);
	}
}
