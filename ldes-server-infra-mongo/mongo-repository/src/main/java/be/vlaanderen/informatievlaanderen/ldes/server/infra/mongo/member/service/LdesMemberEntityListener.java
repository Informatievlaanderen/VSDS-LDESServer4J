package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.service;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.entity.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.membersequence.service.LegacySequenceGeneratorService;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class LdesMemberEntityListener extends AbstractMongoEventListener<LdesMemberEntity> {

	private final LegacySequenceGeneratorService sequenceGenerator;

	public LdesMemberEntityListener(LegacySequenceGeneratorService sequenceGenerator) {
		this.sequenceGenerator = sequenceGenerator;
	}

	@Override
	public void onBeforeConvert(BeforeConvertEvent<LdesMemberEntity> event) {
		if (event.getSource().getSequenceNr() == null) {
			event.getSource().setSequenceNr(sequenceGenerator.generateSequence(event.getSource().getCollectionName()));
		}
	}

}
