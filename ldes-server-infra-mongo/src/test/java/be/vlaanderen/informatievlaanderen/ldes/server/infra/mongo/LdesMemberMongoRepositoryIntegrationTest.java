package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ActiveProfiles("mongo-test")
class LdesMemberMongoRepositoryIntegrationTest {

    @Autowired
    private LdesMemberMongoRepository ldesMemberMongoRepository;

    @Autowired
    private LdesMemberEntityRepository ldesMemberEntityRepository;

    @DisplayName("given object to save" + " when save object using MongoDB template" + " then object is saved")
    @Test
    void when_LdesMembersAreStoredUsingRepository_ObjectsAreStoredInMongoDB() {
        String member = """
                <http://one.example/subject1> <http://one.example/predicate1> <http://one.example/object1>
                <http://example.org/graph1> .""";

        LdesMember ldesMember = new LdesMember(member, Lang.NQUADS);
        ldesMemberMongoRepository.saveLdesMember(ldesMember);
        assertEquals(1, ldesMemberEntityRepository.findAll().size());
        assertEquals(1, ldesMemberMongoRepository.fetchLdesMembers().size());
    }

    @Configuration
    static class TestConfig {

        @Bean
        public LdesMemberMongoRepository ldesMemberMongoRepository(
                final LdesMemberEntityRepository ldesMemberEntityRepository) {
            return new LdesMemberMongoRepository(ldesMemberEntityRepository);
        }

    }

}