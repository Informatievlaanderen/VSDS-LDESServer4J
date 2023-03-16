package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.Shapes;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class LdesShaclValidator extends AbstractShaclValidator {
	private final LdesConfig.Validation validationConfig;

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
		validateShape(member.getModel());
	}

	@Override
	protected void initializeShapes() {
		if (validationConfig.isEnabled() && validationConfig.getShape() != null) {
			shapes = Shapes.parse(RDFDataMgr.loadGraph(validationConfig.getShape()));
		}
	}
}
