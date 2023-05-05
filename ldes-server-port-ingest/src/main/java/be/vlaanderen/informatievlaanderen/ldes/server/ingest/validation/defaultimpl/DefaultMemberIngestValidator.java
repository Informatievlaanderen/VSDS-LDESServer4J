package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;

public class DefaultMemberIngestValidator implements MemberIngestValidator {

	private final ModelIngestValidatorFactory validatorFactory;
	private final Map<String, ModelIngestValidator> validators = new HashMap<>();

	public DefaultMemberIngestValidator(ModelIngestValidatorFactory validatorFactory) {
		this.validatorFactory = validatorFactory;
	}

	// TODO: 5/05/2023 add testing
	@EventListener
	public void handleShaclChangedEvent(ShaclChangedEvent event) {
		final ShaclShape shacl = event.getShacl();
		final String collectionName = shacl.getCollection();

		validators.compute(collectionName,
				(key, oldValue) -> validatorFactory.createValidator(shacl.getModel(), collectionName));
	}

	@Override
	public void validate(Member member) {
		var validator = validators.get(member.getCollectionName());
		if (validator != null) {
			validator.validate(member.getModel());
		}
	}

}
