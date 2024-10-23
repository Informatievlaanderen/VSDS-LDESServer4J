package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral;
import static org.assertj.core.api.Assertions.assertThat;

class LocalDateTimeConverterTest {

	private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

	static Stream<RDFDatatype> dataTypes() {
		return Stream.of(XSDDatatype.XSDdateTime, XSDDatatype.XSDdateTime);
	}

	@ParameterizedTest
	@MethodSource("dataTypes")
	void test_conversionOfXsdTimeToLocalTime(RDFDatatype dataType) {
		LocalDateTime expectedLocalDateTime = LocalDateTime.of(2022, 5, 20, 9, 58, 15);
		Literal typedLiteral = createTypedLiteral("2022-05-20T09:58:15Z", dataType);

		LocalDateTime actualLocalDateTime = localDateTimeConverter.getLocalDateTime(typedLiteral);

		assertThat(actualLocalDateTime).isEqualTo(expectedLocalDateTime);
	}

	@ParameterizedTest
	@MethodSource("dataTypes")
	void test_conversionOfXsdTimeWithoutTimeZone(RDFDatatype dataType) {
		LocalDateTime expectedLocalDateTime = LocalDateTime.of(2023, 4, 14, 12, 10, 30, 629000000);
		Literal typedLiteral = createTypedLiteral("2023-04-14T12:10:30.629238", dataType);

		LocalDateTime actualLocalDateTime = localDateTimeConverter.getLocalDateTime(typedLiteral);

		assertThat(actualLocalDateTime).isEqualTo(expectedLocalDateTime);
	}
}