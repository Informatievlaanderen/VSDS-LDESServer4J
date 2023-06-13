package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.entities.LdesMemberEntityV4;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.membersequence.service.MongockSequenceGeneratorService;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class LdesMemberEntityV4Listener extends AbstractMongoEventListener<LdesMemberEntityV4> {

	private final MongockSequenceGeneratorService sequenceGenerator;

	public LdesMemberEntityV4Listener(MongockSequenceGeneratorService sequenceGenerator) {
		this.sequenceGenerator = sequenceGenerator;
	}

	@Override
	public void onBeforeConvert(BeforeConvertEvent<LdesMemberEntityV4> event) {
		if (event.getSource().getSequenceNr() == null) {
			event.getSource().setSequenceNr(sequenceGenerator.generateSequence(event.getSource().getCollectionName()));
		}
	}

}
