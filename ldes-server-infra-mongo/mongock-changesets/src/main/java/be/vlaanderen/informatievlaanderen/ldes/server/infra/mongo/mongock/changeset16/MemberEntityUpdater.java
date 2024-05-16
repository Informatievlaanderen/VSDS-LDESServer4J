package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset16;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset16.entities.MemberEntityV1;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset16.entities.MemberEntityV2;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@ChangeUnit(id = "member-updater-changeset-16", order = "2024-04-05 00:00:00", author = "VSDS")
public class MemberEntityUpdater {
    public static final String MEMBER_COLLECTION_NAME = "ingest_ldesmember";

    private final MongoTemplate mongoTemplate;

    public MemberEntityUpdater(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void changeSet() {
        mongoTemplate.stream(new Query(), MemberEntityV1.class)
                .map(MemberEntityV2::from)
                .forEach(mongoTemplate::save);
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.stream(new Query(), MemberEntityV2.class)
                .map(MemberEntityV1::from)
                .forEach(mongoTemplate::save);
    }
}
