package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.blanknodevalidators.DcatBlankDataServiceNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators.CannotContainCatalogValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators.CannotContainDatasetValidator;
import org.springframework.stereotype.Component;

@Component
public class DcatDataServiceValidator extends DcatValidator {
	public DcatDataServiceValidator() {
		super(new DcatBlankDataServiceNode(), new CannotContainDatasetValidator(), new CannotContainCatalogValidator());
	}
}
