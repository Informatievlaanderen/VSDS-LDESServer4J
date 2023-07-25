package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.MemberToFragmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

// TODO TVB: 20/07/23 test
public interface MemberToFragmentEntityRepository
		extends MongoRepository<MemberToFragmentEntity, MemberToFragmentEntity.MembersToFragmentEntityId> {

	Optional<MemberToFragmentEntity> findFirstById_ViewNameOrderById_ViewNameAsc(ViewName viewName);

}
