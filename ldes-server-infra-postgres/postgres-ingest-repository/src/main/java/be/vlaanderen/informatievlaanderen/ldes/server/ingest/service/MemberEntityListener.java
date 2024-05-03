package be.vlaanderen.informatievlaanderen.ldes.server.ingest.service;


import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repository.MemberEntityRepository;
import jakarta.persistence.PrePersist;
import org.springframework.stereotype.Component;

@Component
public class MemberEntityListener {


	public static MemberEntityRepository repository;

	@PrePersist
	public void onPrePersist(MemberEntity memberEntity) {
		if (memberEntity.getSequenceNr() == null) {
			Long maxSequenceNr = repository.getNextSequenceNr(memberEntity.getCollectionName());
			memberEntity.setSequenceNr(maxSequenceNr != null ? maxSequenceNr + 1 : 1);
		}
	}

}
