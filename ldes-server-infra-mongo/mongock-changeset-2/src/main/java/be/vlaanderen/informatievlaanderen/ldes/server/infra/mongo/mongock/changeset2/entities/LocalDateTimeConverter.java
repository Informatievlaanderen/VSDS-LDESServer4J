package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset2.entities;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.impl.LiteralImpl;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter {

	public LocalDateTime getLocalDateTime(LiteralImpl literalImpl) {
		RDFDatatype datatype = literalImpl.getDatatype();
		XSDDateTime parse = (XSDDateTime) datatype.parse(literalImpl.getValue().toString());
		return ZonedDateTime.parse(parse.toString(), DateTimeFormatter.ISO_DATE_TIME)
				.toLocalDateTime();
	}
}
