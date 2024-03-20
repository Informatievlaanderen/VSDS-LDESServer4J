package be.vlaanderen.informatievlaanderen.ldes.server.domain.validators;

import org.apache.jena.rdf.model.Model;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@FunctionalInterface
public interface ModelValidator extends Validator {
	void validate(@NotNull Model target);

	default boolean supports(@NotNull Class<?> clazz) {
		return Model.class.isAssignableFrom(clazz);
	}

	@Override
	default void validate(@NotNull Object target, @NotNull Errors errors) {
        Model model = (Model) target;
		validate(model);
	}
}
