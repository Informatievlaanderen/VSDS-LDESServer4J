package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator.ModelIngestValidator;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;

public class MemberIngestValidatorImpl implements MemberIngestValidator {

	private final ModelIngestValidatorFactory validatorFactory;
	private final Map<String, ModelIngestValidator> validators = new HashMap<>();

	public MemberIngestValidatorImpl(ModelIngestValidatorFactory validatorFactory) {
		this.validatorFactory = validatorFactory;
	}

	@EventListener
	public void handleShaclChangedEvent(ShaclChangedEvent event) {
		final String collectionName = event.getCollection();

		validators.compute(collectionName,
				(key, oldValue) -> validatorFactory.createValidator(event.getModel()));
	}

	@EventListener
	public void handleShaclDeletedEvent(ShaclDeletedEvent event) {
		validators.remove(event.collectionName());
	}

	@Override
	public void validate(Member member) {
		var validator = validators.get(member.getCollectionName());
		if (validator != null) {
			validator.validate(member.getModel());
		}
	}

}
