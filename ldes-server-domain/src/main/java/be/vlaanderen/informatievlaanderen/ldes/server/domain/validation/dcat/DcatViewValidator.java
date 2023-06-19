package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.blanknodevalidators.DcatBlankNodeValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators.CannotContainCatalogValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators.CannotContainDatasetValidator;
import org.springframework.stereotype.Component;

@Component
public class DcatViewValidator extends DcatValidator {
	public DcatViewValidator() {
		super(new DcatBlankNodeValidator(DCAT_DATA_SERVICE), new CannotContainDatasetValidator(),
				new CannotContainCatalogValidator());
	}
}
