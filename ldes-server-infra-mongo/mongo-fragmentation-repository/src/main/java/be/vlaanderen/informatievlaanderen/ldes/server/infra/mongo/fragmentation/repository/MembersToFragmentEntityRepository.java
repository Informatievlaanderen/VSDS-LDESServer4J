package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.MembersToFragmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

// TODO TVB: 20/07/23 test
public interface MembersToFragmentEntityRepository
		extends MongoRepository<MembersToFragmentEntity, MembersToFragmentEntity.MembersToFragmentEntityId> {

	Optional<MembersToFragmentEntity> findFirstById_ViewNameOrderById_ViewNameAsc(ViewName viewName);

}
