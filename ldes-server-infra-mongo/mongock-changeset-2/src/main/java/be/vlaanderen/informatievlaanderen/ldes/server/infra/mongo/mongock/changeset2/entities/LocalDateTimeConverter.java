package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset2.entities;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.impl.LiteralImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

public class LocalDateTimeConverter {

	public LocalDateTime getLocalDateTime(LiteralImpl literalImpl) {
		RDFDatatype datatype = literalImpl.getDatatype();
		XSDDateTime parse = (XSDDateTime) datatype.parse(literalImpl.getValue().toString());
		Calendar calendar = parse.asCalendar();
		TimeZone tz = calendar.getTimeZone();
		ZoneId zoneId = tz.toZoneId();
		return LocalDateTime.ofInstant(calendar.toInstant(), zoneId);
	}
}
