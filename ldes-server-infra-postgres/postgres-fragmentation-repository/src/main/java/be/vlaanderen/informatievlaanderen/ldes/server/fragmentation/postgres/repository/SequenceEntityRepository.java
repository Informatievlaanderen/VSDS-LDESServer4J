package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository;


import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.SequenceEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

@Primary
public interface SequenceEntityRepository extends JpaRepository<SequenceEntity, String> {
	void deleteAllByViewNameStartingWith(String collectionName);
}
