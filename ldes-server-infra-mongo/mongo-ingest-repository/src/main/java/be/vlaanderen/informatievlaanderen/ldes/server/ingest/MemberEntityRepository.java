package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface MemberEntityRepository extends MongoRepository<MemberEntity, String> {
	void deleteAllByCollectionName(String collectionName);

	Stream<MemberEntity> getAllByCollectionNameOrderBySequenceNrAsc(String collectionName);

	Stream<MemberEntity> findAllByIdIn(List<String> memberIds);

	Optional<MemberEntity> findFirstByCollectionNameAndInEventSourceAndSequenceNrGreaterThanOrderBySequenceNrAsc(String collectionName,
			boolean inEventSource, long sequenceNr);

	long countByCollectionName(String collectionName);

	Optional<MemberEntity> findFirstByCollectionNameOrderBySequenceNrDesc(String collectionName);

}
