package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import org.apache.jena.rdf.model.Model;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ModelValidator {
    void validate(@NotNull Model target);
}
