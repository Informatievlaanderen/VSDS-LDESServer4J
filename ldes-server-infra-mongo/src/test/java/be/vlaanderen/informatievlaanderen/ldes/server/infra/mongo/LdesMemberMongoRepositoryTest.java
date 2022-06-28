package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfContants.TREE_MEMBER;
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
        String member = String.format("""
                <http://one.example/subject1> <%s> <http://one.example/object1>.""", TREE_MEMBER);

        LdesMember ldesMember = new LdesMember(RdfModelConverter.fromString(member, Lang.NQUADS));
        LdesMemberEntity ldesMemberEntity = LdesMemberEntity.fromLdesMember(ldesMember);
        when(ldesMemberEntityRepository.save(any())).thenReturn(ldesMemberEntity);

        LdesMember actualLdesMember = ldesMemberMongoRepository.saveLdesMember(ldesMember);

        assertTrue(ldesMember.getModel().isIsomorphicWith(actualLdesMember.getModel()));
        verify(ldesMemberEntityRepository, times(1)).save(any());
    }

    @DisplayName("Correct retrieval of all LdesMembers from MongoDB")
    @Test
    void when_LdesMembersAreRetrieved_ListOfAllLdesMemberInDbIsReturned() {
        Model ldesMemberModel = RDFParserBuilder.create().fromString(String.format("""
                <http://one.example/subject1> <%s> <http://one.example/object1> .""", TREE_MEMBER)).lang(Lang.NQUADS)
                .toModel();

        LdesMember ldesMember = new LdesMember(ldesMemberModel);
        LdesMemberEntity ldesMemberEntity = LdesMemberEntity.fromLdesMember(ldesMember);

        Model ldesMemberModel2 = RDFParserBuilder.create().fromString(String.format("""
                <http://one.example/subject1> <%s> <http://one.example/object2> .""", TREE_MEMBER)).lang(Lang.NQUADS)
                .toModel();
        LdesMember ldesMember2 = new LdesMember(ldesMemberModel2);
        LdesMemberEntity ldesMemberEntity2 = LdesMemberEntity.fromLdesMember(ldesMember2);
        when(ldesMemberEntityRepository.findAll()).thenReturn(List.of(ldesMemberEntity, ldesMemberEntity2));

        List<LdesMember> actualLdesMembers = ldesMemberMongoRepository.fetchLdesMembers();

        assertEquals(2, actualLdesMembers.size());
        verify(ldesMemberEntityRepository, times(1)).findAll();
    }

}