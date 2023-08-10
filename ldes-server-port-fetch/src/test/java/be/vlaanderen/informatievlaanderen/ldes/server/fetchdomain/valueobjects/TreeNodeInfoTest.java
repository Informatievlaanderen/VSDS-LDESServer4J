package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFWriter;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeNodeInfoTest {
	private static final String HOST_NAME = "http://localhost:8080";
	private static final String COLLECTION_NAME = "mobility-hindrances";

	@Test
	void when_TreeNodeInfoIsConvertedToStatements_then_ResultingModelIsAsExpected() throws URISyntaxException {
		String treeNodeIdentifier = HOST_NAME + "/" + COLLECTION_NAME + "/node";
		TreeRelation treeRelationOne = getTreeRelationOne();
		TreeRelation treeRelationTwo = getTreeRelationTwo();

		Model actualModel = ModelFactory.createDefaultModel()
				.add(new TreeNodeInfo(treeNodeIdentifier, List.of(treeRelationOne, treeRelationTwo))
						.convertToStatements());

		Model expectedModel = readModelFromFile("valueobjects/treenodeinfo.ttl");
		assertTrue(actualModel.isIsomorphicWith(expectedModel));
	}

	private static TreeRelation getTreeRelationTwo() {
		String treeNodeIdentifierOfSecondRelation = HOST_NAME + "/" + COLLECTION_NAME + "/secondNode";
		return new TreeRelation("http://www.w3.org/ns/prov#generatedAtTime",
				treeNodeIdentifierOfSecondRelation, "value",
				"http://www.w3.org/2001/XMLSchema#dateTime", "https://w3id.org/tree#Relation");
	}

	private static TreeRelation getTreeRelationOne() {
		String treeNodeIdentifierOfFirstRelation = HOST_NAME + "/" + COLLECTION_NAME + "/firstNode";
		return new TreeRelation("http://www.w3.org/ns/prov#generatedAtTime",
				treeNodeIdentifierOfFirstRelation, "value",
				"http://www.w3.org/2001/XMLSchema#dateTime", "https://w3id.org/tree#Relation");
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}
}