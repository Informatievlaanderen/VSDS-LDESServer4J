package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validators.ModelValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validators.ShaclValidator;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;

class ShaclShapeValidatorTest {

    @Nested
    class ShapeValidator {
        private final ModelValidator validator = new ShaclValidator("validator-shapes/shapeShaclShape.ttl");

        @Test
        void test_classSupport() {
            Model model = ModelFactory.createDefaultModel();

            assertThat(validator.supports(model.getClass())).isTrue();
            assertThat(validator.supports(Model.class)).isTrue();

            assertThat(validator.supports(String.class)).isFalse();
            assertThat(validator.supports(Object.class)).isFalse();
        }

        @Test
        void when_ValidateValidShaclShape_thenReturnValid() throws URISyntaxException {
            final Model validShaclShape = RDFDataMgr.loadModel("eventstream/streams/valid-shape.ttl");

            assertThatNoException().isThrownBy(() -> validator.validate(validShaclShape));
        }

        @Test
        void when_validateInvalidShaclShape_thenReturnInvalid() {
            final Model model = RDFDataMgr.loadModel("eventstream/streams/invalid-shape.ttl");

            assertThatThrownBy(() -> validator.validate(model)).isInstanceOf(ShaclValidationException.class);
        }
    }

    @Nested
    class ViewShapeValidator {
        private final ModelValidator validator = new ShaclValidator("validator-shapes/viewShaclShape.ttl");

        @Test
        void given_ValidViewWithHierarchicalFragmentation_when_validateView_then_ThrowNoException() {
            final Model model = RDFDataMgr.loadModel("view-with-hierarchical-timebased-frag.ttl");

            assertThatNoException().isThrownBy(() -> validator.validate(model));
        }

        @Test
        void given_InvalidViewWithHierarchicalFragmentation_when_validateView_then_ThrowNoException() throws IOException {
            final File file = ResourceUtils.getFile("classpath:view-with-hierarchical-timebased-frag.ttl");
            final String modelString = FileUtils.readFileToString(file, StandardCharsets.UTF_8).replace("day", "invalid-value");
            final Model model = RDFParser.fromString(modelString).lang(Lang.TURTLE).toModel();

            assertThatThrownBy(() -> validator.validate(model))
                    .isInstanceOf(ShaclValidationException.class)
                    .hasMessageContaining("tree:maxGranularity");
        }
    }

}
