package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.DcatNodeValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.DcatValidator.DCAT_DATA_SERVICE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.DcatValidator.DCAT_DATA_SERVICE_PREDICATE;

public class CannotContainServiceValidator implements DcatNodeValidator {
	private final List<CannotContainRule> rules;

	public CannotContainServiceValidator() {
		rules = List.of(
				dcat -> !dcat.listSubjectsWithProperty(RDF.type, DCAT_DATA_SERVICE).hasNext(),
				dcat -> !dcat.listSubjectsWithProperty(DCAT_DATA_SERVICE_PREDICATE).hasNext());
	}

	@Override
	public void validate(Model dcat) {
		boolean isValid = rules.stream()
				.map(rule -> rule.evaluate(dcat))
				.reduce(true, (prevResult, result) -> prevResult && result);

		if (!isValid) {
			throw new IllegalArgumentException("Model cannot contain any kind of relation to dcat:DataService.");
		}
	}
}
