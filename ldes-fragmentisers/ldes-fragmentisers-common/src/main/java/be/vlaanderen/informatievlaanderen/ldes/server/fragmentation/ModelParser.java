package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.LocalDateTimeConverter;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.LiteralImpl;

import java.time.LocalDateTime;
import java.util.List;

public class ModelParser {
	private static final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

	private ModelParser() {
	}

	public static Object getFragmentationObject(Model model, String subjectFilter, String fragmentationPredicate) {
		return getFragmentationObjects(model, subjectFilter, fragmentationPredicate)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public static List<Object> getFragmentationObjects(Model model, String subjectFilter, String fragmentationPath) {
		return model
				.listStatements(null, ResourceFactory.createProperty(fragmentationPath), (Resource) null)
				.toList()
				.stream()
				.filter(statement -> statement.getSubject().toString().matches(subjectFilter))
				.map(Statement::getObject)
				.map(RDFNode::asLiteral)
				.map(Literal::getValue)
				.toList();
	}

	public static LocalDateTime getFragmentationObjectLocalDateTime(Model model, String subjectFilter,
			String fragmentationPredicate) {
		return getFragmentationObjectsLocalDateTime(model, subjectFilter, fragmentationPredicate)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public static List<LocalDateTime> getFragmentationObjectsLocalDateTime(Model model, String subjectFilter,
			String fragmentationPath) {
		return model
				.listStatements(null, ResourceFactory.createProperty(fragmentationPath), (Resource) null)
				.toList()
				.stream()
				.filter(statement -> statement.getSubject().toString().matches(subjectFilter))
				.map(Statement::getObject)
				.map(RDFNode::asLiteral)
				.map(literal -> localDateTimeConverter.getLocalDateTime((LiteralImpl) literal))
				.toList();
	}
}