package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LdesMemberMongoRepositoryTest {
    private final LdesMemberEntityRepository ldesMemberEntityRepository = mock(LdesMemberEntityRepository.class);
    private final LdesMemberMongoRepository ldesMemberMongoRepository = new LdesMemberMongoRepository(
            ldesMemberEntityRepository);

    @DisplayName("Correct saving of an LdesMember in MongoDB")
    @Test
    void when_LdesMemberIsSavedInRepository_CreatedResourceIsReturned() {
        String member = """
                <http://one.example/subject1> <http://one.example/predicate1> <http://one.example/object1>
                <http://example.org/graph1> .""";

        LdesMember ldesMember = new LdesMember(member, Lang.NQUADS);
        LdesMemberEntity ldesMemberEntity = LdesMemberEntity.fromLdesMember(ldesMember);
        when(ldesMemberEntityRepository.save(any())).thenReturn(ldesMemberEntity);

        LdesMember actualLdesMember = ldesMemberMongoRepository.saveLdesMember(ldesMember);

        assertTrue(ldesMember.getModel().isIsomorphicWith(actualLdesMember.getModel()));
        verify(ldesMemberEntityRepository, times(1)).save(any());
    }

    @DisplayName("Correct retrieval of all LdesMembers from MongoDB")
    @Test
    void when_LdesMembersAreRetrieved_ListOfAllLdesMemberInDbIsReturned() {
        Model ldesMemberModel = RDFParserBuilder.create().fromString("""
                <http://one.example/subject1> <http://one.example/predicate1> <http://one.example/object1>
                <http://example.org/graph1> .""").lang(Lang.NQUADS).toModel();

        LdesMember ldesMember = new LdesMember(ldesMemberModel);
        LdesMemberEntity ldesMemberEntity = LdesMemberEntity.fromLdesMember(ldesMember);

        Model ldesMemberModel2 = RDFParserBuilder.create().fromString("""
                <http://one.example/subject2> <http://one.example/predicate2> <http://one.example/object2>
                <http://example.org/graph2> .""").lang(Lang.NQUADS).toModel();
        LdesMember ldesMember2 = new LdesMember(ldesMemberModel2);
        LdesMemberEntity ldesMemberEntity2 = LdesMemberEntity.fromLdesMember(ldesMember2);
        when(ldesMemberEntityRepository.findAll()).thenReturn(List.of(ldesMemberEntity, ldesMemberEntity2));

        List<LdesMember> actualLdesMembers = ldesMemberMongoRepository.fetchLdesMembers();

        assertEquals(2, actualLdesMembers.size());
        verify(ldesMemberEntityRepository, times(1)).findAll();
    }
}