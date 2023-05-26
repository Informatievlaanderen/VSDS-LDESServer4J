package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.blanknodevalidators.DcatBlankDatasetNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators.CannotContainCatalogValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.cannotcontainvalidators.CannotContainServiceValidator;
import org.springframework.stereotype.Component;

@Component
public class DcatDatasetValidator extends DcatValidator {
	public DcatDatasetValidator() {
		super(new DcatBlankDatasetNode(), new CannotContainCatalogValidator(), new CannotContainServiceValidator());
	}
}
