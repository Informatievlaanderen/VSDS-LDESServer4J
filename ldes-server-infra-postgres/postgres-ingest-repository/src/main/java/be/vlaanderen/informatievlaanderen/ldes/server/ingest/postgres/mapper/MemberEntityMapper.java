package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import org.springframework.stereotype.Component;

@Component
public class MemberEntityMapper {
	public IngestedMember toMember(MemberEntity memberEntity) {
		return new IngestedMember(
				memberEntity.getSubject(),
				memberEntity.getCollection().getName(),
				memberEntity.getVersionOf(),
				memberEntity.getTimestamp(),
				memberEntity.getSequenceNr(),
				memberEntity.isInEventSource(),
				memberEntity.getTransactionId(),
				memberEntity.getModel()
		);
	}

}
