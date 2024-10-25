package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_VALUE;
import static org.apache.jena.rdf.model.ResourceFactory.*;
import static org.assertj.core.api.Assertions.allOf;
import static org.assertj.core.api.Assertions.assertThat;

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

		assertThat(statements)
				.hasSize(5)
				.has(relationStatements());
	}

	@Test
	void given_ByReferenceTreeNode_when_ConvertToStatements_then_StatementsContainTreeValueAsResource() {
		TreeRelationResponse treeRelation = new TreeRelationResponse("path",
				HOST_NAME + "/" + COLLECTION_NAME + "/node", "https://data.vlaanderen.be/ns/verkeersmetingen#Verkeersmeetpunt",
				XSDDatatype.XSDanyURI.getURI(), "relation");

		List<Statement> statements = treeRelation
				.convertToStatements(HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME);

		assertThat(statements)
				.hasSize(5)
				.filteredOn(statement -> statement.getPredicate().equals(TREE_VALUE))
				.first()
				.matches(statement -> statement.getObject().isResource());
	}


	private Condition<List<? extends Statement>> relationStatements() {
		return allOf(
				containingSubject(createResource(HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME), createProperty("https://w3id.org/tree#relation")),
				containingObject(RDF.type, createResource("relation")),
				containingObject(createProperty("https://w3id.org/tree#path"), createProperty("path")),
				containingObject(createProperty("https://w3id.org/tree#node"), createResource("http://localhost:8080/mobility-hindrances/node")),
				containingObject(createProperty("https://w3id.org/tree#value"), createTypedLiteral("value", XSDDatatype.XSDdateTime))
		);
	}

	private Condition<List<? extends Statement>> containingSubject(Resource subject, Property predicate) {
		return new Condition<>(
				statements -> statements.stream()
						.anyMatch(statement -> statement.getSubject().equals(subject) && statement.getPredicate().equals(predicate)),
				"TreeNode must contain statement with subject %s and predicate %s", subject, predicate
		);
	}

	private Condition<List<? extends Statement>> containingObject(Property predicate, RDFNode object) {
		return new Condition<>(
				statements -> statements.stream()
						.anyMatch(statement -> statement.getPredicate().equals(predicate) && statement.getObject().equals(object)),
				"TreeNode must contain statement with predicate %s and object %s", predicate, object
		);
	}

}