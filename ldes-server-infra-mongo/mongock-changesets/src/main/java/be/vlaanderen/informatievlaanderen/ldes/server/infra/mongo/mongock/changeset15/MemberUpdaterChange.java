package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset12.entities.EventStreamEntityV1;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset12.entities.EventStreamEntityV2;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.entities.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.entities.MemberEntityV1;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.entities.MemberEntityV2;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.services.MemberV2Builder;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.valueobjects.EventStreamProperties;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Map;
import java.util.stream.Collectors;

public class MemberUpdaterChange {
    public static final String MEMBER_COLLECTION_NAME = "ingest_ldesmember";

    private final MongoTemplate mongoTemplate;

    public MemberUpdaterChange(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void changeSet() {
        final Map<String, EventStreamProperties> eventstreamProperties = fetchEventStreamProperties();

         mongoTemplate.stream(new Query(), MemberEntityV1.class)
                .map(memberEntityV1 -> MemberV2Builder
                        .createWithEventStreamProperties(eventstreamProperties.get(memberEntityV1.getCollectionName()))
                        .with(memberEntityV1)
                        .build())
                .forEach(mongoTemplate::save);
    }

    private Map<String, EventStreamProperties> fetchEventStreamProperties() {
        return mongoTemplate.stream(new Query(), EventStreamEntity.class)
                .collect(Collectors.toMap(
                        EventStreamEntity::getId,
                        eventStreamEntity -> new EventStreamProperties(eventStreamEntity.getVersionOfPath(), eventStreamEntity.getTimestampPath())
                ));
    }

    @RollbackExecution
    public void rollback() {
        final var query = new Query();
        final var update = new Update().unset("versionOf").unset("timestamp").unset("transactionId");
        final var bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MEMBER_COLLECTION_NAME);
        bulkOps.updateMulti(query, update);
        bulkOps.execute();
    }
}
