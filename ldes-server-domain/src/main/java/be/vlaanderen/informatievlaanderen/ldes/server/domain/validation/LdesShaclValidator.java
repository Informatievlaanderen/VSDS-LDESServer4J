package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.Shapes;
import org.springframework.validation.Errors;

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

	public void validate(Object target) {
		validate(target, null);
	}

	@Override
	protected void initializeShapes() {
		if (validationConfig.isEnabled() && validationConfig.getShape() != null) {
			shapes = Shapes.parse(RDFDataMgr.loadGraph(validationConfig.getShape()));
		}
	}
}
