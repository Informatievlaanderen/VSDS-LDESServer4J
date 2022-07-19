package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfConstants.TREE_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ActiveProfiles("mongo-test")
class LdesMemberMongoRepositoryIT {

    private final String MEMBER_TYPE = "https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder";

    @Autowired
    private LdesMemberMongoRepository ldesMemberMongoRepository;

    @Autowired
    private LdesMemberEntityRepository ldesMemberEntityRepository;

    @DisplayName("given object to save" + " when save object using MongoDB template" + " then object is saved")
    @Test
    void when_LdesMembersAreStoredUsingRepository_ObjectsAreStoredInMongoDB() {
        String member = String.format("""
                <http://one.example/subject1> <%s> <http://one.example/object1>.""", TREE_MEMBER);

        LdesMember ldesMember = new LdesMember(RdfModelConverter.fromString(member, Lang.NQUADS));
        ldesMemberMongoRepository.saveLdesMember(ldesMember, MEMBER_TYPE);
        assertEquals(1, ldesMemberEntityRepository.findAll().size());
        assertEquals(1, ldesMemberMongoRepository.fetchLdesMembers().size());
    }
}