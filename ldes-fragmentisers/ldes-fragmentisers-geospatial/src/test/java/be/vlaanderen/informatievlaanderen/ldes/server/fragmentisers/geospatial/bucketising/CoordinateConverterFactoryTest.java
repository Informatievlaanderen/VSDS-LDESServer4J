package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateConverterFactoryTest {

	@Test
	void when_SupportedProjectionIsRequested_CoordinateConverterIsReturned() {
		CoordinateConverter coordinateConverter = CoordinateConverterFactory.getCoordinateConverter("lambert72");
		assertTrue(coordinateConverter instanceof Lambert72CoordinateConverter);
	}

	@Test
	void when_UnsupportedProjectionIsRequested_IllegalArgumentExceptionIsThrown() {
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> CoordinateConverterFactory.getCoordinateConverter("mercator"));
		assertEquals("No coordinateConverter for projection: mercator", illegalArgumentException.getMessage());
	}

}