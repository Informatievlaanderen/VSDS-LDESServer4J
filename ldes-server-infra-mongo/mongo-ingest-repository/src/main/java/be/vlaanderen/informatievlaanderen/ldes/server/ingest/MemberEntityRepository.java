package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.stream.Stream;

public interface MemberEntityRepository extends MongoRepository<MemberEntity, String> {
	void deleteAllByCollectionName(String collectionName);

	// TODO: 06/07/23 test
	Stream<MemberEntity> getAllByCollectionNameOrderBySequenceNrAsc(String collectionName);
}
