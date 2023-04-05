package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LdesShaclValidator implements Validator {
	private Shapes shapes;
	private final LdesConfig.Validation validationConfig;
	private boolean initialized;

	public LdesShaclValidator(final LdesConfig ldesConfig) {
		validationConfig = ldesConfig.validation();
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Member.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Member member = (Member) target;
		validateShape(member);
	}

	protected void validateShape(Member member) {
		if (!initialized) {
			if (validationConfig.isEnabled() && validationConfig.getShape() != null) {
				shapes = Shapes.parse(RDFDataMgr.loadGraph(validationConfig.getShape()));
			}
			initialized = true;
		}
		if (shapes != null) {
			ValidationReport report = ShaclValidator.get().validate(shapes, member.getModel().getGraph());

			if (!report.conforms()) {
				throw new LdesShaclValidationException(RdfModelConverter.toString(report.getModel(), Lang.TURTLE));
			}
		}
	}
}
