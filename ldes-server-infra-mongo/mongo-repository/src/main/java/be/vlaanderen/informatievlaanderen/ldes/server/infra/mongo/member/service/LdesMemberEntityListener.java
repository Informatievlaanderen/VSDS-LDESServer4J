package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.service;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.entity.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.membersequence.service.SequenceGeneratorService;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class LdesMemberEntityListener extends AbstractMongoEventListener<LdesMemberEntity> {

	private final SequenceGeneratorService sequenceGenerator;

	public LdesMemberEntityListener(SequenceGeneratorService sequenceGenerator) {
		this.sequenceGenerator = sequenceGenerator;
	}

	@Override
	public void onBeforeConvert(BeforeConvertEvent<LdesMemberEntity> event) {
		if (event.getSource().getSequenceNr() == null) {
			event.getSource().setSequenceNr(sequenceGenerator.generateSequence(event.getSource().getCollectionName()));
		}
	}

}
