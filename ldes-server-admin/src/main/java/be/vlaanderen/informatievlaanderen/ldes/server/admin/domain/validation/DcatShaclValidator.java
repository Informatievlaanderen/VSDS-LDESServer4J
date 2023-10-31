package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.shacl.Shapes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DcatShaclValidator extends ShaclValidator {

    public DcatShaclValidator(@Value("${ldes-server.dcat-shape:}") String dcatShapeUri) {
        super(dcatShapeUri);
    }

    @Override
    protected void initializeShapes() {
        if (StringUtils.isNotBlank(shapesFileUri)) {
            super.shapes = Shapes.parse(Objects.requireNonNull(shapesFileUri));
        }
    }
}
