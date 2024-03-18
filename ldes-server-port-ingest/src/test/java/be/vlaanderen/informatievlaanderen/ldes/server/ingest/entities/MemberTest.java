package be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member.TREE_MEMBER;
import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    @DisplayName("Test correct replacing of TreeMember statement")
    void when_TreeMemberStatementIsReplaced_TreeMemberStatementHasADifferentSubject() {
        Model model = RDFParser.source("example-ldes-member.nq").build().toModel();
        Member member = new Member(
                "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1", "collectionName",
                "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464",
                LocalDateTime.parse("2020-12-28T09:36:37.127"),
                0L, UUID.randomUUID().toString(), model);

        member.removeTreeMember();
        Statement statement = member.getModel().listStatements(null, TREE_MEMBER, (Resource) null).nextOptional()
                .orElse(null);

        assertNull(statement);
    }

    @Test
    void testGetters() {
        Model model = RDFParser.source("example-ldes-member.nq").build().toModel();
        Member member = new Member(
                "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1", "collectionName",
                "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464",
                LocalDateTime.parse("2020-12-28T09:36:37.127"),
                0L, UUID.randomUUID().toString(), model);

        assertEquals("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
                member.getId());
        assertEquals(0L, member.getSequenceNr());
        assertEquals("collectionName", member.getCollectionName());
        assertTrue(member.getModel().isIsomorphicWith(model));
    }

    @ParameterizedTest
    @ArgumentsSource(EqualityTestProvider.class)
    void testEqualsAndHashCode(BiConsumer<Object, Object> assertion, Member a, Member b) {
        assertNotNull(assertion);
        assertion.accept(a, b);
        if (a != null && b != null) {
            assertion.accept(a.hashCode(), b.hashCode());
        }
    }

    static class EqualityTestProvider implements ArgumentsProvider {

        private static final String idA = "idA";
        private static final Member memberA = new Member(idA, null, null, null, null, null, null);

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(equals(), memberA, memberA),
                    Arguments.of(equals(), new Member(idA, "otherCollection", "other", LocalDateTime.now(), 10L, "txId", ModelFactory.createDefaultModel()),
                            memberA),
                    Arguments.of(notEquals(),
                            new Member("idB", "otherCollection", "other", LocalDateTime.now(), 10L, "txId", ModelFactory.createDefaultModel()), memberA),
                    Arguments.of(notEquals(), new Member("idB", null, null, null, null, null, null), memberA));
        }

        private static BiConsumer<Object, Object> equals() {
            return Assertions::assertEquals;
        }

        private static BiConsumer<Object, Object> notEquals() {
            return Assertions::assertNotEquals;
        }

    }

}