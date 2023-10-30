package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.shacl.Shapes;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventStreamValidator extends AbstractShaclValidator {
	public static final String FILE_NAME = "eventstreamShaclShape.ttl";

	@Override
	protected void initializeShapes() {
		shapes = Shapes.parse(FILE_NAME);
	}

    @Override
	public void validate(@NotNull Object target, @NotNull Errors errors) {
		Model eventStream = (Model) target;
		validate(eventStream);
	}
}
