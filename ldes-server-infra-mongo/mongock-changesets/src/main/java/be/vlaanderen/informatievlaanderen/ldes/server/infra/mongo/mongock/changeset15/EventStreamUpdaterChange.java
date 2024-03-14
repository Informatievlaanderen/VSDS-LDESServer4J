package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ChangeUnit(id = "eventstream-updater-changeset-15", order = "2024-03-14 00:00:00", author = "VSDS")
public class EventStreamUpdaterChange {

    private static final Logger log = LoggerFactory.getLogger(be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset15.EventStreamUpdaterChange.class);

    private final MongoTemplate mongoTemplate;

    public EventStreamUpdaterChange(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void changeSet() {
        final var query = new Query();
        final var update = new Update().set("versionCreationEnabled", false);
        final var bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "eventstream");
        bulkOps.updateMulti(query, update);

        final var result = bulkOps.execute();
        log.atInfo().log("Added 'versionCreationEnabled' with default value 'false'. to {} entities", result.getModifiedCount());
    }

    @RollbackExecution
    public void rollback() {
        final var query = new Query();
        final var update = new Update().unset("versionCreationEnabled");
        final var bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "eventstream");
        bulkOps.updateMulti(query, update);

        final var result = bulkOps.execute();
        log.atInfo().log("Removed 'versionCreationEnabled' from {} entities.", result.getModifiedCount());
    }

}