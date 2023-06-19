package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.pointintime.PointInTimeRetentionPolicy;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.LiteralImpl;
import org.apache.jena.riot.Lang;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class PointInTimeRetentionPolicyCreator implements RetentionPolicyCreator {
	public static final Property LDES_POINT_IN_TIME = createProperty(LDES, "pointInTime");

	@Override
	public RetentionPolicy createRetentionPolicy(Model model) {
		List<RDFNode> ldesPointInTimes = model.listObjectsOfProperty(LDES_POINT_IN_TIME).toList();
		if (ldesPointInTimes.size() != 1) {
			throw new IllegalArgumentException(
					"Cannot Create Point-In-Time Retention Policy in which there is not exactly 1 "
							+ LDES_POINT_IN_TIME.toString()
							+ " statement.\n Found " + ldesPointInTimes.size() + " statements in :\n"
							+ RdfModelConverter.toString(model, Lang.TURTLE));
		}
		LiteralImpl ldesPointInTimeObject = (LiteralImpl) ldesPointInTimes.get(0);
		LocalDateTime localDateTime = getLocalDateTime(ldesPointInTimeObject);
		return new PointInTimeRetentionPolicy(localDateTime);
	}

	public LocalDateTime getLocalDateTime(LiteralImpl literalImpl) {
		RDFDatatype datatype = literalImpl.getDatatype();
		XSDDateTime parse = (XSDDateTime) datatype.parse(literalImpl.getValue().toString());
		Calendar calendar = parse.asCalendar();
		TimeZone tz = calendar.getTimeZone();
		ZoneId zoneId = tz.toZoneId();
		return LocalDateTime.ofInstant(calendar.toInstant(), zoneId);
	}
}
