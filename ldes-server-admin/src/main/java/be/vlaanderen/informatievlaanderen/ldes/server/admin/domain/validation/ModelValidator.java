package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import org.apache.jena.rdf.model.Model;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Validator;

public interface ModelValidator extends Validator {
    void validate(@NotNull Model target);

    default boolean supports(@NotNull Class<?> clazz) {
        return Model.class.isAssignableFrom(clazz);
    }
}
