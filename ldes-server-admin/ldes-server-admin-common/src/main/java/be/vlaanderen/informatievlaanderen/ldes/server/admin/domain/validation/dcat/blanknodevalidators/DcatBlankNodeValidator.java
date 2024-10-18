package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.dcat.blanknodevalidators;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.dcat.DcatNodeValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.util.List;

public class DcatBlankNodeValidator implements DcatNodeValidator {
	private static final String TOO_MANY_ERROR_MESSAGE = "Model must include exactly one dcat:%s. Not more, not less.";
	private static final String MUST_BE_BLANK_ERROR_MESSAGE = "Node of type dcat:%s must be a blank node";
	private final Property subject;

	public DcatBlankNodeValidator(Property subject) {
		this.subject = subject;
	}

	@Override
	public void validate(Model dcat) {
		final List<Resource> resources = dcat.listSubjectsWithProperty(RDF.type, subject).toList();
		if (resources.size() != 1) {
			throw new IllegalArgumentException(String.format(TOO_MANY_ERROR_MESSAGE, subject.getLocalName()));
		}

		if (!resources.get(0).asNode().isBlank()) {
			throw new IllegalArgumentException(String.format(MUST_BE_BLANK_ERROR_MESSAGE, subject.getLocalName()));
		}
	}
}
