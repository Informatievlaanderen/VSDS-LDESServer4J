package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.stream.Stream;

public interface MemberEntityRepository extends MongoRepository<MemberEntity, String> {
	void deleteAllByCollectionName(String collectionName);

	Stream<MemberEntity> getAllByCollectionNameOrderBySequenceNrAsc(String collectionName);
}
