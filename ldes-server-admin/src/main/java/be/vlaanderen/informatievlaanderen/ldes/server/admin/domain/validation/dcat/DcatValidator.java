package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.dcat;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validators.ModelValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.dcat.blanknodevalidators.DcatBlankNodeValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;

import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public abstract class DcatValidator implements ModelValidator {
	public static final String DCAT = "http://www.w3.org/ns/dcat#";
	public static final Property DCAT_DATA_SERVICE = createProperty(DCAT, "DataService");
	public static final Property DCAT_DATASET = createProperty(DCAT, "Dataset");
	public static final Property DCAT_CATALOG = createProperty(DCAT, "Catalog");
	public static final Property DCAT_SERVES_DATASET = createProperty(DCAT, "servesDataset");
	public static final Property DCAT_DATASET_PREDICATE = createProperty(DCAT, "dataset");
	public static final Property DCAT_DATA_SERVICE_PREDICATE = createProperty(DCAT, "service");

	private final DcatBlankNodeValidator blankNodeValidator;
	private final List<DcatNodeValidator> cannotContainValidators;

	protected DcatValidator(DcatBlankNodeValidator blankNodeValidator,
			DcatNodeValidator... cannotContainValidators) {
		this.blankNodeValidator = blankNodeValidator;
		this.cannotContainValidators = List.of(cannotContainValidators);
	}

	@Override
	public void validate(@NotNull Object target, @NotNull Errors errors) {
		final Model dcat = (Model) target;
		validate(dcat);
	}

	@Override
	public void validate(@NotNull Model dcat) {
		blankNodeValidator.validate(dcat);
		cannotContainValidators.forEach(validator -> validator.validate(dcat));
	}

}