package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LdesShaclValidator implements Validator {
	private Shapes shapes;
	private static final Logger LOGGER = LoggerFactory.getLogger(LdesShaclValidator.class);

	public LdesShaclValidator(final LdesConfig ldesConfig) {
		if (ldesConfig.validation().isEnabled() && ldesConfig.validation().getShape() != null) {
			shapes = Shapes.parse(RDFDataMgr.loadGraph(ldesConfig.validation().getShape()));
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Member.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Member member = (Member) target;
		validateShape(member, errors);
	}

	protected void validateShape(Member member, Errors errors) {
		if (shapes != null) {
			ValidationReport report = ShaclValidator.get().validate(shapes, member.getModel().getGraph());

			if (!report.conforms()) {
				errors.reject("shape.invalid");
				LOGGER.error(RdfModelConverter.toString(report.getModel(), Lang.TTL));
			}
		}
	}
}
