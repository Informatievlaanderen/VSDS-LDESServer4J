package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TreeRelationTest {
	private static final String HOST_NAME = "http://localhost:8080";
	private static final String COLLECTION_NAME = "mobility-hindrances";

	@Test
	void when_TreeRelationIsConvertedToStatements_then_ResultingModelIsAsExpected() throws URISyntaxException {
		String treeNodeIdentifierOfRelation = HOST_NAME + "/" + COLLECTION_NAME + "/otherNode";
		String treeNodeIdentifier = HOST_NAME + "/" + COLLECTION_NAME + "/node";
		TreeRelation treeRelation = new TreeRelation("http://www.w3.org/ns/prov#generatedAtTime",
				treeNodeIdentifierOfRelation, "value",
				"http://www.w3.org/2001/XMLSchema#dateTime", "https://w3id.org/tree#Relation");

		Model actualModel = ModelFactory.createDefaultModel().add(treeRelation
				.convertToStatements(treeNodeIdentifier));

		Model expectedModel = readModelFromFile("valueobjects/treerelation.ttl");
		assertTrue(actualModel.isIsomorphicWith(expectedModel));
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}
}