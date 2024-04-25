package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;


import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.SequenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SequenceEntityRepository extends JpaRepository<SequenceEntity, String> {
	void deleteAllByViewNameStartingWith(String collectionName);
}
