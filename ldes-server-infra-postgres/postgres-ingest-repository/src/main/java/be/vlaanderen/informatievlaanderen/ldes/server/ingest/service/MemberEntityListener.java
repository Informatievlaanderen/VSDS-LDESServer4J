package be.vlaanderen.informatievlaanderen.ldes.server.ingest.service;


import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;

@Component
public class MemberEntityListener {

	public static EntityManager entityManager;

	@PrePersist
	public void onPrePersist(MemberEntity memberEntity) {
		if (memberEntity.getSequenceNr() == null) {
			Query query = entityManager.createQuery("SELECT MAX(m.sequenceNr) FROM MemberEntity m WHERE m.collectionName = :collectionName");
			query.setParameter("collectionName", memberEntity.getCollectionName());
			Long maxSequenceNr = (Long) query.getSingleResult();
			memberEntity.setSequenceNr(maxSequenceNr != null ? maxSequenceNr + 1 : 1);
		}
	}

}
