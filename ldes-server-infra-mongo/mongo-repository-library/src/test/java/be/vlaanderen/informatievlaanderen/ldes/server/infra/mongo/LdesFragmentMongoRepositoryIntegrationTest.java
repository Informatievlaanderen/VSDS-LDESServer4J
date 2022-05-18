package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.config.EndpointConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.services.LdesFragmentCreator;
import org.json.simple.JSONObject;
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

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ActiveProfiles("mongo-test")
class LdesFragmentMongoRepositoryIntegrationTest {

    @Autowired
    private LdesMemberMongoRepository ldesFragmentMongoRepository;

    @Autowired
    private LdesFragmentRepository ldesFragmentRepository;

    @DisplayName("given object to save" + " when save object using MongoDB template" + " then object is saved")
    @Test
    void when_FragmentsAreStoredUsingRepository_ObjectsAreStoredInMongoDB() {
        JSONObject jsonObject1 = createLdesFragment("ldesFragmentKeyOne", "ldesFragmentValueOne");
        JSONObject jsonObject2 = createLdesFragment("ldesFragmentKeyTwo", "ldesFragmentValueTwo");

        ldesFragmentMongoRepository.saveLdesMember(jsonObject1);
        ldesFragmentMongoRepository.saveLdesMember(jsonObject2);

        assertEquals(2, ldesFragmentRepository.findAll().size());

        JSONObject retrievedFirstPage = ldesFragmentMongoRepository.retrieveLdesFragmentsPage(0);
        assertTrue(retrievedFirstPage.toJSONString().contains("ldesFragmentValueOne"));
        JSONObject retrievedSecondPage = ldesFragmentMongoRepository.retrieveLdesFragmentsPage(1);
        assertTrue(retrievedSecondPage.toJSONString().contains("ldesFragmentValueTwo"));
    }

    private JSONObject createLdesFragment(String ldesFragmentKeyOne, String ldesFragmentValueOne) {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put(ldesFragmentKeyOne, ldesFragmentValueOne);
        return jsonObject1;
    }

    @Configuration
    static class TestConfig {

        @Bean
        public LdesMemberMongoRepository ldesFragmentMongoRepository(
                final LdesFragmentRepository ldesFragmentRepository) {
            final LdesFragmentCreator fragmentCreator = new LdesFragmentCreator(new EndpointConfig("localhost"));
            return new LdesMemberMongoRepository(ldesFragmentRepository, fragmentCreator);
        }

    }

}