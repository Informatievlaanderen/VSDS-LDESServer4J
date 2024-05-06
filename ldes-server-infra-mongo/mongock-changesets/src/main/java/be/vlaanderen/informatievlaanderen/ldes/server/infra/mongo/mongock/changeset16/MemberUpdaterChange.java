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

@ChangeUnit(id = "eventstream-updater-changeset-16", order = "2024-05-06 00:00:00", author = "VSDS")
public class MemberUpdaterChange {

    public static final String MEMBER_COLLECTION_NAME = "ingest_ldesmember";

    private static final Logger log = LoggerFactory.getLogger(MemberUpdaterChange.class);

    private final MongoTemplate mongoTemplate;

    public MemberUpdaterChange(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void changeSet() {
        final var query = new Query();
        final var update = new Update().set("isInEventSource", true);
        final var bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MEMBER_COLLECTION_NAME);
        bulkOps.updateMulti(query, update);

        final var result = bulkOps.execute();
        log.atInfo().log("Added 'isInEventSource' with default value true to {} entities", result.getModifiedCount());
    }
    @RollbackExecution
    public void rollback() {
        final var query = new Query();
        final var update = new Update().unset("isInEventSource");
        final var bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MEMBER_COLLECTION_NAME);
        bulkOps.updateMulti(query, update);
        bulkOps.execute();
    }
}
