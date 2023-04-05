package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import org.junit.jupiter.api.Test;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.Projections.LAMBERT72;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.Projections.NOOP;
import static org.junit.jupiter.api.Assertions.*;

class CoordinateConverterFactoryTest {

	@Test
	void when_SupportedProjectionIsRequested_CoordinateConverterIsReturned() {
		CoordinateConverter coordinateConverter = CoordinateConverterFactory.getCoordinateConverter(LAMBERT72.name());
		assertTrue(coordinateConverter instanceof Lambert72CoordinateConverter);
	}

	@Test
	void when_NoopProjectionIsRequested_CoordinateConverterIsReturned() {
		CoordinateConverter coordinateConverter = CoordinateConverterFactory.getCoordinateConverter(NOOP.name());
		assertTrue(coordinateConverter instanceof NoopCoordinateConverter);
	}

	@Test
	void when_UnsupportedProjectionIsRequested_IllegalArgumentExceptionIsThrown() {
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> CoordinateConverterFactory.getCoordinateConverter("mercator"));
		assertEquals("No coordinateConverter for projection: mercator", illegalArgumentException.getMessage());
	}

}