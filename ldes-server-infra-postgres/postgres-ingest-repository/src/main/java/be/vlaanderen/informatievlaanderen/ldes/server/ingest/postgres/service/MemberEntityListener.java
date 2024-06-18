package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.service;


import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import jakarta.persistence.PrePersist;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({"java:S14444", "java:S1104"})
public class MemberEntityListener {

	private final ConfigurableApplicationContext applicationContext;

	public MemberEntityListener(ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

    @PrePersist
	public void onPrePersist(MemberEntity memberEntity) {
		if (memberEntity.getSequenceNr() == null) {
			final MemberEntityRepository repository = applicationContext.getBean(MemberEntityRepository.class);
			Long maxSequenceNr = repository.getNextSequenceNr(memberEntity.getCollectionName());
			memberEntity.setSequenceNr(maxSequenceNr != null ? maxSequenceNr + 1 : 1);
		}
	}

}
