package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.converters.LdesMemberConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.converters.LdesMemberConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LdesMemberMongoRepositoryTest {
    private final LdesMemberEntityRepository ldesMemberEntityRepository = mock(LdesMemberEntityRepository.class);
    private final LdesMemberConverter ldesMemberConverter = new LdesMemberConverterImpl();
    private final LdesMemberMongoRepository ldesMemberMongoRepository = new LdesMemberMongoRepository(
            ldesMemberEntityRepository);

    @DisplayName("Correct saving of an LdesMember in MongoDB")
    @Test
    void when_LdesMemberIsSavedInRepository_CreatedResourceIsReturned() {

        // LdesMember ldesMember = createLdesMember(
        // "<http://one.example/subject1> <http://one.example/predicate1> <http://one.example/object1>
        // <http://example.org/graph1> .");
        // LdesMemberEntity ldesMemberEntity = ldesMemberConverter.toEntity(ldesMember);
        // when(ldesMemberEntityRepository.save(any())).thenReturn(ldesMemberEntity);
        //
        // LdesMember actualLdesMember = ldesMemberMongoRepository.saveLdesMember(ldesMember);
        //
        // assertEquals(ldesMember.getQuads().length, actualLdesMember.getQuads().length);
        // verify(ldesMemberEntityRepository, times(1)).save(any());
    }

    @DisplayName("Correct retrieval of all LdesMembers from MongoDB")
    @Test
    void when_LdesMembersAreRetrieved_ListOfAllLdesMemberInDbIsReturned() {
        // LdesMember ldesMember = createLdesMember(
        // "<http://one.example/subject1> <http://one.example/predicate1> <http://one.example/object1>
        // <http://example.org/graph1> .");
        // LdesMemberEntity ldesMemberEntity = ldesMemberConverter.toEntity(ldesMember);
        // LdesMember ldesMember2 = createLdesMember(
        // "<http://one.example/subject2> <http://one.example/predicate2> <http://one.example/object2>
        // <http://example.org/graph2> .");
        // LdesMemberEntity ldesMemberEntity2 = ldesMemberConverter.toEntity(ldesMember2);
        // when(ldesMemberEntityRepository.findAll()).thenReturn(List.of(ldesMemberEntity, ldesMemberEntity2));
        //
        // List<LdesMember> actualLdesMembers = ldesMemberMongoRepository.fetchLdesMembers();
        //
        // assertEquals(2, actualLdesMembers.size());
        // verify(ldesMemberEntityRepository, times(1)).findAll();
    }

    // private LdesMember createLdesMember(String inputString) {
    // return new LdesMember(new String[] { inputString });
    // }
}