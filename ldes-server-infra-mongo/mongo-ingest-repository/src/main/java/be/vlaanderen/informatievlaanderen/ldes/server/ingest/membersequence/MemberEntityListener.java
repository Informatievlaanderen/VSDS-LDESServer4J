package be.vlaanderen.informatievlaanderen.ldes.server.ingest.membersequence;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class MemberEntityListener extends AbstractMongoEventListener<MemberEntity> {

	private final IngestMemberSequenceService sequenceGenerator;

	public MemberEntityListener(IngestMemberSequenceService sequenceGenerator) {
		this.sequenceGenerator = sequenceGenerator;
	}

	@Override
	public void onBeforeConvert(BeforeConvertEvent<MemberEntity> event) {
		if (event.getSource().getSequenceNr() == null) {
			event.getSource().setSequenceNr(sequenceGenerator.generateSequence(event.getSource().getCollectionName()));
		}
	}

}
