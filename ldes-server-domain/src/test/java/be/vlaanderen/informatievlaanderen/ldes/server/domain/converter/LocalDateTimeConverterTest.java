package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.impl.LiteralImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral;
import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeConverterTest {

	private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

	@Test
	void test_conversionOfXsdTimeToLocalTime() {
		LiteralImpl typedLiteral = (LiteralImpl) createTypedLiteral("2022-05-20T09:58:15Z",
				TypeMapper.getInstance().getTypeByName("http://www.w3.org/2001/XMLSchema#dateTime"));

		LocalDateTime actualLocalDateTime = localDateTimeConverter.getLocalDateTime(typedLiteral);

		LocalDateTime expectedLocalDateTime = LocalDateTime.of(2022, 5, 20, 9, 58, 15);
		assertEquals(expectedLocalDateTime, actualLocalDateTime);
	}

	@Test
	void test_conversionOfXsdTimeWithoutTimeZone() {
		LiteralImpl typedLiteral = (LiteralImpl) createTypedLiteral("2023-04-14T12:10:30.629238",
				TypeMapper.getInstance().getTypeByName("http://www.w3.org/2001/XMLSchema#dateTime"));

		LocalDateTime actualLocalDateTime = localDateTimeConverter.getLocalDateTime(typedLiteral);

		LocalDateTime expectedLocalDateTime = LocalDateTime.of(2023, 4, 14, 12, 10, 30, 629000000);
		assertEquals(expectedLocalDateTime, actualLocalDateTime);
	}
}