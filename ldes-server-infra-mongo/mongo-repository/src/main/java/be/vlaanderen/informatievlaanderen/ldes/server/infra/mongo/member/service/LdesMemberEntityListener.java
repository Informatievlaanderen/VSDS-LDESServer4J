package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.service;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.entity.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.membersequence.service.DeprecatedSequenceGeneratorService;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public class LdesMemberEntityListener extends AbstractMongoEventListener<LdesMemberEntity> {

	private final DeprecatedSequenceGeneratorService sequenceGenerator;

	public LdesMemberEntityListener(DeprecatedSequenceGeneratorService sequenceGenerator) {
		this.sequenceGenerator = sequenceGenerator;
	}

	@Override
	public void onBeforeConvert(BeforeConvertEvent<LdesMemberEntity> event) {
		if (event.getSource().getSequenceNr() == null) {
			event.getSource().setSequenceNr(sequenceGenerator.generateSequence(event.getSource().getCollectionName()));
		}
	}

}
