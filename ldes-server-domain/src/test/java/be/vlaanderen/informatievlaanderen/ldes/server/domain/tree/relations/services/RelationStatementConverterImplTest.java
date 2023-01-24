package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RelationStatementConverterImplTest {

	private static final String HOST_NAME = "http://localhost:8080";
	private static final String COLLECTION_NAME = "mobility-hindrances";
	private static final String VIEW_NAME = "view";

	private RelationStatementConverter relationStatementConverter;

	@BeforeEach
	void setUp() {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setCollectionName(COLLECTION_NAME);
		ldesConfig.setHostName(HOST_NAME);
		ldesConfig.setShape("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape");
		ldesConfig.setMemberType("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder");
		ldesConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
		ldesConfig.setVersionOf("http://purl.org/dc/terms/isVersionOf");
		relationStatementConverter = new RelationStatementConverterImpl(ldesConfig);
	}

	@Test
	void when_RelationsAreMissing_NoStatementsAreReturned() {
		Resource fragmentId = createResource(HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME);

		List<Statement> statements = relationStatementConverter.getRelationStatements(List.of(), fragmentId);

		assertTrue(statements.isEmpty());
	}

	@Test
	void when_RelationsAreNotEmpty_MultipleStatementsAreReturned() {
		TreeRelation treeRelation = new TreeRelation("path", "/node", "value",
				"http://www.w3.org/2001/XMLSchema#dateTime", "relation");

		Resource fragmentId = createResource(HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME);

		List<Statement> statements = relationStatementConverter.getRelationStatements(List.of(treeRelation),
				fragmentId);

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