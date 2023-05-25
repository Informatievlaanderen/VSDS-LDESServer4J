package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.blanknodevalidators.DcatBlankCatalogNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators.CannotContainDatasetValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators.CannotContainServiceValidator;
import org.springframework.stereotype.Component;

@Component
public class DcatCatalogValidator extends DcatValidator {
	DcatCatalogValidator() {
		super(new DcatBlankCatalogNode(), new CannotContainDatasetValidator(), new CannotContainServiceValidator());
	}
}
