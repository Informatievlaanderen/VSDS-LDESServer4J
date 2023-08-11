package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeNodeTest {

	private static final String HOST_NAME = "http://localhost:8080";
	private static final String COLLECTION_NAME = "mobility-hindrances";
	private TreeNode treeNode;

	@BeforeEach
	void setUp() {
		String eventStreamIdentifier = HOST_NAME + "/" + COLLECTION_NAME;
		String treeNodeIdentifier = HOST_NAME + "/" + COLLECTION_NAME + "/node";
		EventStreamInfo eventStreamInfo = getEventStreamInfo(treeNodeIdentifier, eventStreamIdentifier);
		TreeNodeInfo treeNodeInfo = getTreeNodeInfo(treeNodeIdentifier);
		TreeMemberList treeMemberList = getTreeMemberList(eventStreamIdentifier);
		treeNode = new TreeNode(eventStreamInfo, treeNodeInfo, treeMemberList);
	}

	@Test
	void when_TreeNodeIsConvertedToStatements_then_ResultingModelIsAsExpected() throws URISyntaxException {
		Model actualModel = treeNode.getModel();

		Model expectedModel = readModelFromFile("valueobjects/treenode.ttl");
		assertTrue(actualModel.isIsomorphicWith(expectedModel));
	}

	@Test
	void when_TreeNodeGetTreeNodeIdsInRelations_then_ListOfTreeNodeIdsInRelationsIsReturned() {
		List<String> treeNodeIdsInRelations = treeNode.getTreeNodeIdsInRelations();

		List<String> expectedTreeNodeIdsInRelations = List.of("http://localhost:8080/mobility-hindrances/firstNode",
				"http://localhost:8080/mobility-hindrances/secondNode");
		assertEquals(expectedTreeNodeIdsInRelations, treeNodeIdsInRelations);
	}

	@Test
	void when_TreeNodeGetMemberIds_then_ListOfMemberIdsIsReturned() {
		List<String> memberIds = treeNode.getMemberIds();

		List<String> expectedMemberIds = List.of("http://localhost:8080/mobility-hindrances/1",
				"http://localhost:8080/mobility-hindrances/2");
		assertEquals(expectedMemberIds, memberIds);
	}

	private TreeMemberList getTreeMemberList(String eventStreamIdentifier) {
		TreeMember treeMemberOne = getTreeMemberOne();
		TreeMember treeMemberTwo = getTreeMemberTwo();
		return new TreeMemberList(eventStreamIdentifier,
				List.of(treeMemberOne, treeMemberTwo));
	}

	private TreeNodeInfo getTreeNodeInfo(String treeNodeIdentifier) {
		TreeRelation treeRelationOne = getTreeRelationOne();
		TreeRelation treeRelationTwo = getTreeRelationTwo();
		return new TreeNodeInfo(treeNodeIdentifier, List.of(treeRelationOne, treeRelationTwo));
	}

	private TreeRelation getTreeRelationTwo() {
		String treeNodeIdentifierOfSecondRelation = HOST_NAME + "/" + COLLECTION_NAME + "/secondNode";
		return new TreeRelation("http://www.w3.org/ns/prov#generatedAtTime",
				treeNodeIdentifierOfSecondRelation, "value",
				"http://www.w3.org/2001/XMLSchema#dateTime", "https://w3id.org/tree#Relation");
	}

	private TreeRelation getTreeRelationOne() {
		String treeNodeIdentifierOfFirstRelation = HOST_NAME + "/" + COLLECTION_NAME + "/firstNode";
		return new TreeRelation("http://www.w3.org/ns/prov#generatedAtTime",
				treeNodeIdentifierOfFirstRelation, "value",
				"http://www.w3.org/2001/XMLSchema#dateTime", "https://w3id.org/tree#Relation");
	}

	private EventStreamInfo getEventStreamInfo(String treeNodeIdentifier, String eventStreamIdentifier) {
		Model shacl = RDFParserBuilder.create().fromString(
				"""
						<http://localhost:8080/mobility-hindrances> <https://w3id.org/tree#shape> <http://localhost:8080/mobility-hindrances/shape>.""")
				.lang(Lang.NQUADS).toModel();
		Model dcat = RDFParserBuilder.create().fromString(
				"""
						<http://localhost:8080/mobility-hindrances> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#DataService>.""")
				.lang(Lang.NQUADS).toModel();
		EventStreamProperties eventStreamProperties = new EventStreamProperties(eventStreamIdentifier,
				"http://www.w3.org/ns/prov#generatedAtTime", "http://purl.org/dc/terms/isVersionOf");
		return new EventStreamInfo(treeNodeIdentifier, eventStreamIdentifier, shacl, false,
				dcat.listStatements().toList(), eventStreamProperties);
	}

	private TreeMember getTreeMemberTwo() {
		Model modelTwo = RDFParserBuilder.create().fromString("""
				<http://localhost:8080/mobility-hindrances/2> <http://schema.org/name> "Hindrance2".""")
				.lang(Lang.NQUADS).toModel();
		return new TreeMember("http://localhost:8080/mobility-hindrances/2", modelTwo);
	}

	private TreeMember getTreeMemberOne() {
		Model modelOne = RDFParserBuilder.create().fromString("""
				<http://localhost:8080/mobility-hindrances/1> <http://schema.org/name> "Hindrance1".""")
				.lang(Lang.NQUADS).toModel();
		return new TreeMember("http://localhost:8080/mobility-hindrances/1", modelOne);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}

}