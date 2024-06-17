package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.service;


import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import jakarta.persistence.PrePersist;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({"java:S14444", "java:S1104"})
public class MemberEntityListener {

	public static MemberEntityRepository repository;

	@PrePersist
	public void onPrePersist(MemberEntity memberEntity) {
		if (memberEntity.getSequenceNr() == null) {
			Long maxSequenceNr = repository.getNextSequenceNr(memberEntity.getCollection().getName());
			memberEntity.setSequenceNr(maxSequenceNr != null ? maxSequenceNr + 1 : 1);
		}
	}

}
