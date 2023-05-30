package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.blanknodevalidators.DcatBlankNodeValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators.CannotContainDatasetValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators.CannotContainServiceValidator;
import org.springframework.stereotype.Component;

@Component
public class DcatCatalogValidator extends DcatValidator {
	public DcatCatalogValidator() {
		super(new DcatBlankNodeValidator(DCAT_CATALOG), new CannotContainDatasetValidator(),
				new CannotContainServiceValidator());
	}
}
