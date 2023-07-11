package be.vlaanderen.informatievlaanderen.ldes.server.domain;

import org.apache.jena.rdf.model.*;

import java.util.List;

public class ModelParser {
	private ModelParser() {
	}
	public static Object getFragmentationObject(Model model, String subjectFilter, String fragmentationPredicate) {
		// @formatter:off
		return getFragmentationObjects(model, subjectFilter, fragmentationPredicate)
				.stream()
				.findFirst()
				.orElse(null);
		// @formatter:on
	}

	public static List<Object> getFragmentationObjects(Model model, String subjectFilter, String fragmentationPath) {
		// @formatter:off
		return model
				.listStatements(null, ResourceFactory.createProperty(fragmentationPath), (Resource) null)
				.toList()
				.stream()
				.filter(statement -> statement.getSubject().toString().matches(subjectFilter))
				.map(Statement::getObject)
				.map(RDFNode::asLiteral)
				.map(Literal::getValue)
				.toList();
		// @formatter:on
	}
}