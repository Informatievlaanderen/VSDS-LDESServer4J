package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.blanknodevalidators.DcatBlankNodeValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators.CannotContainValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public abstract class DcatValidator implements Validator {
	public static final String DCAT = "http://www.w3.org/ns/dcat#";
	public static final Property DCAT_DATA_SERVICE = createProperty(DCAT, "DataService");
	public static final Property DCAT_DATASET = createProperty(DCAT, "Dataset");
	public static final Property DCAT_CATALOG = createProperty(DCAT, "Catalog");
	public static final Property DCAT_SERVES_DATASET = createProperty(DCAT, "servesDataset");
	public static final Property DCAT_DATASET_PREDICATE = createProperty(DCAT, "dataset");
	public static final Property DCAT_DATA_SERVICE_PREDICATE = createProperty(DCAT, "service");

	private final DcatBlankNodeValidator blankNodeValidator;
	private final List<CannotContainValidator> cannotContainValidators;

	protected DcatValidator(DcatBlankNodeValidator blankNodeValidator,
			CannotContainValidator... cannotContainValidators) {
		this.blankNodeValidator = blankNodeValidator;
		this.cannotContainValidators = List.of(cannotContainValidators);
	}

	@Override
	public void validate(Object target, Errors errors) {
		final Model dcat = (Model) target;
		validate(dcat);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Model.class.isAssignableFrom(clazz);
	}

	protected void validate(Model dcat) {
		blankNodeValidator.validateBlankNode(dcat);
		cannotContainValidators.forEach(validator -> validator.validate(dcat));
	}

}