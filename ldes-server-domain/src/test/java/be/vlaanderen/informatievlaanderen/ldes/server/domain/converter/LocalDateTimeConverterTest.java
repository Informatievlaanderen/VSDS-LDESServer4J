package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral;
import static org.assertj.core.api.Assertions.*;

class LocalDateTimeConverterTest {

	private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

	static Stream<RDFDatatype> datatypes() {
		return Stream.of(XSDDatatype.XSDdateTime, XSDDatatype.XSDstring);
	}

	static Stream<Literal> invalidDatatypes() {
		return Stream.of(
				createTypedLiteral("1729774515", XSDDatatype.XSDint),
				createTypedLiteral("2022-05-20", XSDDatatype.XSDdate),
				createTypedLiteral("09:58:15Z", XSDDatatype.XSDtime),
				createTypedLiteral("true", XSDDatatype.XSDboolean)
		);
	}

	@ParameterizedTest
	@MethodSource("datatypes")
	void test_conversionOfXsdTimeToLocalTime(RDFDatatype dataType) {
		LocalDateTime expectedLocalDateTime = LocalDateTime.of(2022, 5, 20, 9, 58, 15);
		Literal typedLiteral = createTypedLiteral("2022-05-20T09:58:15Z", dataType);

		LocalDateTime actualLocalDateTime = localDateTimeConverter.getLocalDateTime(typedLiteral);

		assertThat(actualLocalDateTime).isEqualTo(expectedLocalDateTime);
	}

	@ParameterizedTest
	@MethodSource("datatypes")
	void test_conversionOfXsdTimeToLocalTimeWithOffset(RDFDatatype dataType) {
		LocalDateTime expectedLocalDateTime = LocalDateTime.of(2022, 5, 20, 8, 58, 15);
		Literal typedLiteral = createTypedLiteral("2022-05-20T09:58:15+01:00", dataType);

		LocalDateTime actualLocalDateTime = localDateTimeConverter.getLocalDateTime(typedLiteral);

		assertThat(actualLocalDateTime).isEqualTo(expectedLocalDateTime);
	}

	@ParameterizedTest
	@MethodSource("datatypes")
	void test_conversionOfXsdTimeWithoutTimeZone(RDFDatatype dataType) {
		LocalDateTime expectedLocalDateTime = LocalDateTime.of(2023, 4, 14, 12, 10, 30, 629000000);
		Literal typedLiteral = createTypedLiteral("2023-04-14T12:10:30.629238", dataType);

		LocalDateTime actualLocalDateTime = localDateTimeConverter.getLocalDateTime(typedLiteral);

		assertThat(actualLocalDateTime)
				.isCloseTo(expectedLocalDateTime, within(238000, ChronoUnit.NANOS));
	}

	@ParameterizedTest
	@MethodSource("invalidDatatypes")
	void test_conversionOfInvalidTimestamp(Literal literalWithInvalidDatatype) {
		assertThatThrownBy(() -> localDateTimeConverter.getLocalDateTime(literalWithInvalidDatatype))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Provided datatype cannot be used for conversion: " + literalWithInvalidDatatype.getDatatype());
	}

	@ParameterizedTest
	@ValueSource(strings = {"not-a-timestamp", "2022-05-20T09:58:15Z+1:00", "2022-05-20T09:58:15Z[UTC]", "2022-05-20T09:58:15.999999999999999"})
	void test_conversionOfInvalidValues(String value) {
		Literal literalWithInvalidValue = createTypedLiteral(value, XSDDatatype.XSDstring);

		assertThatThrownBy(() -> localDateTimeConverter.getLocalDateTime(literalWithInvalidValue))
				.isInstanceOf(DateTimeParseException.class);
	}

}