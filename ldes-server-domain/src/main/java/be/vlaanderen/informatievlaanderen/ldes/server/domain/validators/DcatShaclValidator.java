package be.vlaanderen.informatievlaanderen.ldes.server.domain.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DcatShaclValidator extends ShaclValidator {

    public DcatShaclValidator(@Value("${ldes-server.dcat-shape:}") String dcatShapeUri) {
        super(dcatShapeUri);
    }

    @Override
    protected void initializeShapes() {
        if (StringUtils.isNotBlank(shapesFileUri)) {
            super.initializeShapes();
        }
    }
}
