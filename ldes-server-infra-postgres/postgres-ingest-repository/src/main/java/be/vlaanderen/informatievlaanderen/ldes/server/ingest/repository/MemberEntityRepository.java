package be.vlaanderen.informatievlaanderen.ldes.server.ingest.repository;


import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface MemberEntityRepository extends JpaRepository<MemberEntity, String> {
	@Query("SELECT MAX(m.sequenceNr) FROM MemberEntity m WHERE m.collectionName = :collectionName")
	Long getNextSequenceNr(String collectionName);

	boolean existsByIdIn(List<String> strings);
	void deleteAllByCollectionName(String collectionName);

	List<MemberEntity> getAllByCollectionNameOrderBySequenceNrAsc(String collectionName);

	List<MemberEntity> findAllByIdIn(List<String> memberIds);

	Optional<MemberEntity> findMemberEntityByCollectionNameAndSequenceNr(String collectionName, long sequenceNr);

	Optional<MemberEntity> findFirstByCollectionNameAndSequenceNrGreaterThanOrderBySequenceNrAsc(String collectionName,
			long sequenceNr);

	long countByCollectionName(String collectionName);

	Optional<MemberEntity> findFirstByCollectionNameOrderBySequenceNrDesc(String collectionName);

}
