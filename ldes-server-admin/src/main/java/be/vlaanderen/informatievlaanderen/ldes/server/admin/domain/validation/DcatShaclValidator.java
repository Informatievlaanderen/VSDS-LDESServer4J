package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.shacl.Shapes;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Objects;

@Component
public class DcatShaclValidator extends AbstractShaclValidator {
    private final String dcatShapeUri;

    public DcatShaclValidator(@Value("${ldes-server.dcat-shape:}") String dcatShapeUri) {
        this.dcatShapeUri = dcatShapeUri;
    }

    @Override
    protected void initializeShapes() {
        if (isValidatorActive()) {
            shapes = Shapes.parse(Objects.requireNonNull(dcatShapeUri));
        }
    }

    @Override
    public void validate(@NotNull Object target, @NotNull Errors errors) {
        if (isValidatorActive()) {
            Model model = (Model) target;
            validate(model);
        }
    }

    private boolean isValidatorActive() {
        return StringUtils.isNotBlank(dcatShapeUri);
    }
}
