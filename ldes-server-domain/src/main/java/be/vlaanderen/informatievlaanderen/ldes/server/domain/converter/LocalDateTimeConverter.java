package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Literal;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.TimeZone;

public class LocalDateTimeConverter {

	public LocalDateTime getLocalDateTime(Literal literal) {
		RDFDatatype datatype = literal.getDatatype();
		if (XSDDatatype.XSDdateTime.equals(datatype)) {
			XSDDateTime dateTime = (XSDDateTime) literal.getValue();
			return fromXsdDateTime(dateTime);
		}
		return fromString(literal.getString());
	}

	private LocalDateTime fromXsdDateTime(XSDDateTime dateTime) {
		Calendar calendar = dateTime.asCalendar();
		TimeZone tz = calendar.getTimeZone();
		ZoneId zoneId = tz.toZoneId();
		return LocalDateTime.ofInstant(calendar.toInstant(), zoneId);
	}

	private LocalDateTime fromString(String dateTime) {
		final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				.append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
				.appendPattern("[XXX][X]")
				.toFormatter();
		TemporalAccessor temporalAccessor = formatter.parseBest(dateTime, ZonedDateTime::from, LocalDateTime::from);
		return switch (temporalAccessor) {
			case ZonedDateTime zonedDateTime -> zonedDateTime.toLocalDateTime();
			case LocalDateTime localDateTime -> localDateTime;
			default -> throw new IllegalArgumentException("Could not parse date time: " + dateTime);
		};
	}
}
