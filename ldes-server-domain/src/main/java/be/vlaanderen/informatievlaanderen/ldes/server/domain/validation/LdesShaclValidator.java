package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.shacl.Shapes;
import org.springframework.validation.Errors;

public class LdesShaclValidator extends AbstractShaclValidator {
	private final Model shaclShape;

	public LdesShaclValidator(final Model shaclShape) {
		this.shaclShape = shaclShape;
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
		if (shaclShape != null) {
			shapes = Shapes.parse(shaclShape);
		}

	}
}
