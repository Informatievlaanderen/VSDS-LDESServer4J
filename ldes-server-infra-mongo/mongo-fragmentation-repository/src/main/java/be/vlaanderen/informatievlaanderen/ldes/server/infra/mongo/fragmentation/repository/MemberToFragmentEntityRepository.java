package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.MemberToFragmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MemberToFragmentEntityRepository
		extends MongoRepository<MemberToFragmentEntity, String> {

	Optional<MemberToFragmentEntity> findFirstByViewNameOrderBySequenceNrAsc(String viewName);

}
