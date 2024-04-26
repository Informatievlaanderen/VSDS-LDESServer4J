package be.vlaanderen.informatievlaanderen.ldes.server.ingest.repository;



import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


public interface MemberEntityRepository extends JpaRepository<MemberEntity, String> {
	void deleteAllByCollectionName(String collectionName);

	Stream<MemberEntity> getAllByCollectionNameOrderBySequenceNrAsc(String collectionName);

	Stream<MemberEntity> findAllByIdIn(List<String> memberIds);

	Optional<MemberEntity> findMemberEntityByCollectionNameAndSequenceNr(String collectionName, long sequenceNr);

	Optional<MemberEntity> findFirstByCollectionNameAndSequenceNrGreaterThanOrderBySequenceNrAsc(String collectionName,
			long sequenceNr);

	long countByCollectionName(String collectionName);

	Optional<MemberEntity> findFirstByCollectionNameOrderBySequenceNrDesc(String collectionName);

}
