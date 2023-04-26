package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstream.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstream.entity.EventStreamEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventStreamEntityRepository extends MongoRepository<EventStreamEntity, String> {
}
