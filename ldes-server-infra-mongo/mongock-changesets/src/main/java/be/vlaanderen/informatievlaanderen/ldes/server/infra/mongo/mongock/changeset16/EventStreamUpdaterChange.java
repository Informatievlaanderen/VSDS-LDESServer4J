package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset16;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@ChangeUnit(id = "eventstream-updater-changeset-16", order = "2024-05-06 00:00:20", author = "VSDS")
public class EventStreamUpdaterChange {
    public static final String EVENT_STREAM_COLLECTION_NAME = "eventstream";

    private static final Logger log = LoggerFactory.getLogger(be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset16.EventStreamUpdaterChange.class);

    private final MongoTemplate mongoTemplate;

    public EventStreamUpdaterChange(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void changeSet() {
        final var query = new Query();
        final var update = new Update().set("retentionPolicies", List.of());
        final var bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, EVENT_STREAM_COLLECTION_NAME);
        bulkOps.updateMulti(query, update);

        final var result = bulkOps.execute();
        log.atInfo().log("Added empty list 'retentionPolicies'. to {} entities", result.getModifiedCount());
    }

    @RollbackExecution
    public void rollback() {
        final var query = new Query();
        final var update = new Update().unset("retentionPolicies");
        final var bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, EVENT_STREAM_COLLECTION_NAME);
        bulkOps.updateMulti(query, update);

        final var result = bulkOps.execute();
        log.atInfo().log("Removed 'retentionPolicies' from {} entities.", result.getModifiedCount());
    }

}
