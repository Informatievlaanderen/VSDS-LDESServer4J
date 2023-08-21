package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import org.apache.jena.rdf.model.Statement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeRelationResponseTest {

    private static final String HOST_NAME = "http://localhost:8080";
    private static final String COLLECTION_NAME = "mobility-hindrances";
    private static final String VIEW_NAME = "view";

    @Test
    void when_RelationsAreNotEmpty_MultipleStatementsAreReturned() {
        TreeRelationResponse treeRelation = new TreeRelationResponse("path",
                HOST_NAME + "/" + COLLECTION_NAME + "/node", "value",
                "http://www.w3.org/2001/XMLSchema#dateTime", "relation");

        List<Statement> statements = treeRelation
                .convertToStatements(HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME);

        assertEquals(5, statements.size());
        verifyRelationStatements(statements);
    }

    private void verifyRelationStatements(List<Statement> statements) {
        List<String> statementsAsStrings = statements.stream().map(Statement::toString).toList();
        String anonymousObjectId = statements.get(0).getObject().toString();

        assertTrue(statementsAsStrings.contains(
                String.format(
                        "[" + HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME
                                + ", https://w3id.org/tree#relation, %s]",
                        anonymousObjectId)));
        assertTrue(statementsAsStrings.contains(
                String.format("[%s, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, relation]", anonymousObjectId)));

        assertTrue(statementsAsStrings.contains(String.format("[%s, https://w3id.org/tree#path, path]",
                anonymousObjectId)));

        assertTrue(statementsAsStrings.contains(
                String.format("[%s, https://w3id.org/tree#node, http://localhost:8080/mobility-hindrances/node]",
                        anonymousObjectId)));

        assertTrue(statementsAsStrings.contains(
                String.format("[%s, https://w3id.org/tree#value, \"value\"^^http://www.w3.org/2001/XMLSchema#dateTime]",
                        anonymousObjectId)));
    }

}