package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class DcatCatalogValidator implements Validator {
	// TODO: merge or rebase feat/dcat_validation onto this branch

	@Override
	public boolean supports(Class<?> clazz) {
		return false;
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO: merge or rebase feat/dcat_validation onto this branch
	}
}
