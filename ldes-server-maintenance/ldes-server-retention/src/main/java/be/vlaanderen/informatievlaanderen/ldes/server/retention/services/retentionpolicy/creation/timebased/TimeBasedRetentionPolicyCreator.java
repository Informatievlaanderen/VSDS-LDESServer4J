package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDuration;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.LiteralImpl;
import org.apache.jena.riot.Lang;

import java.time.Duration;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class TimeBasedRetentionPolicyCreator implements RetentionPolicyCreator {
	public static final Property TREE_VALUE = createProperty(TREE, "value");

	@Override
	public RetentionPolicy createRetentionPolicy(Model model) {
		List<RDFNode> treeValueStatements = model.listObjectsOfProperty(TREE_VALUE).toList();
		if (treeValueStatements.size() != 1) {
			throw new IllegalArgumentException(
					"Cannot Create Time Based Retention Policy in which there is not exactly 1 " + TREE_VALUE.toString()
							+ " statement.\n Found " + treeValueStatements.size() + " statements in :\n"
							+ RdfModelConverter.toString(model, Lang.TURTLE));
		}
		LiteralImpl object = (LiteralImpl) treeValueStatements.get(0);
		Duration localDateTime = getDurations(object);
		return new TimeBasedRetentionPolicy(localDateTime);
	}

	public Duration getDurations(LiteralImpl literalImpl) {
		RDFDatatype datatype = literalImpl.getDatatype();
		XSDDuration xsdDuration = (XSDDuration) datatype.parse(literalImpl.getValue().toString());
		return DurationParser.parseText(xsdDuration.toString());
	}
}
