package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TreeNodeConverterImplTest {

	private static final String HOST_NAME = "http://localhost:8080";
	private static final String COLLECTION_NAME = "mobility-hindrances";
	private static final String PREFIX = HOST_NAME + "/" + COLLECTION_NAME + "/";
	private static final String VIEW_NAME = "view";
	private final PrefixAdder prefixAdder = new PrefixAdderImpl();
	private TreeNodeConverter treeNodeConverter;

	@BeforeEach
	void setUp() {
		LdesConfig ldesConfig = new LdesConfig();
		LdesSpecification ldesSpecification = new LdesSpecification();
		ldesConfig.setLdesStreams(List.of(ldesSpecification));
		ldesSpecification.setCollectionName(COLLECTION_NAME);
		ldesSpecification.setHostName(HOST_NAME);
		ldesSpecification.validation()
				.setShape("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape");
		ldesSpecification.setMemberType("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder");
		ldesSpecification.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
		ldesSpecification.setVersionOf("http://purl.org/dc/terms/isVersionOf");
		treeNodeConverter = new TreeNodeConverterImpl(prefixAdder, ldesConfig);
	}

	@Test
	void when_TreeNodeHasNoMembersAndIsAView_ModelHasTreeNodeAndLdesStatements() {
		TreeNode treeNode = new TreeNode(PREFIX + VIEW_NAME, false, false, true, List.of(), List.of(),
				COLLECTION_NAME);
		Model model = treeNodeConverter.toModel(treeNode);

		assertEquals(6, getNumberOfStatements(model));
		verifyTreeNodeStatement(model);
		verifyLdesStatements(model);
	}

	@Test
	void when_TreeNodeHasNoMembersAndIsNotAView_ModelHasTreeNodeAndPartOfStatements() {
		TreeNode treeNode = new TreeNode(PREFIX + VIEW_NAME, false, false, false, List.of(), List.of(),
				COLLECTION_NAME);
		Model model = treeNodeConverter.toModel(treeNode);

		assertEquals(2, getNumberOfStatements(model));
		verifyTreeNodeStatement(model);
		verifyIsPartOfStatement(model);
	}

	@Test
	void when_TreeNodeHasMembersAndARelations_ModelHasMultipleStatements() {
		Model ldesMemberModel = RDFParserBuilder.create().fromString("""
				<http://localhost:8080/mobility-hindrances> <https://w3id.org/tree#member>
				<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165>
				.""").lang(Lang.NQUADS).toModel();
		Member member = new Member(
				COLLECTION_NAME,
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165", null, null,
				ldesMemberModel,
				List.of());
		TreeRelation treeRelation = new TreeRelation("path", "/node", "value",
				"http://www.w3.org/2001/XMLSchema#dateTime", "relation");
		TreeNode treeNode = new TreeNode(PREFIX + VIEW_NAME, false, false, false, List.of(treeRelation),
				List.of(member), COLLECTION_NAME);

		Model model = treeNodeConverter.toModel(treeNode);

		assertEquals(9, getNumberOfStatements(model));
		verifyTreeNodeStatement(model);
		verifyIsPartOfStatement(model);
		Resource relationObject = model.listStatements(null, TREE_RELATION,
				(Resource) null).nextStatement().getObject()
				.asResource();
		verifyRelationStatements(model, relationObject);
		verifyMemberStatements(model);
	}

	private void verifyLdesStatements(Model model) {
		String id = HOST_NAME + "/" + COLLECTION_NAME;

		assertEquals("[" + id + ", http://www.w3.org/1999/02/22-rdf-syntax-ns#type, https://w3id.org/ldes#EventStream]",
				model.listStatements(createResource(id), RDF_SYNTAX_TYPE, (Resource) null).nextStatement().toString());
		assertEquals("[" + id + ", https://w3id.org/ldes#timestampPath, http://www.w3.org/ns/prov#generatedAtTime]",
				model.listStatements(createResource(id), LDES_TIMESTAMP_PATH, (Resource) null).nextStatement()
						.toString());
		assertEquals("[" + id + ", https://w3id.org/ldes#versionOfPath, http://purl.org/dc/terms/isVersionOf]",
				model.listStatements(createResource(id), LDES_VERSION_OF, (Resource) null).nextStatement().toString());
		assertEquals("[" + id
				+ ", https://w3id.org/tree#shape, https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape]",
				model.listStatements(createResource(id), TREE_SHAPE, (Resource) null).nextStatement().toString());

		verifyIsViewOfStatement(model);
	}

	private void verifyRelationStatements(Model model, Resource relationObject) {
		assertEquals(
				String.format(
						"[" + HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME
								+ ", https://w3id.org/tree#relation, %s]",
						relationObject),
				model.listStatements(createResource(HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME), TREE_RELATION,
						(Resource) null)
						.nextStatement().toString());
		assertEquals(String.format("[%s, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, relation]", relationObject),
				model.listStatements(relationObject, RDF_SYNTAX_TYPE, (Resource) null).nextStatement().toString());
		assertEquals(String.format("[%s, https://w3id.org/tree#path, path]", relationObject),
				model.listStatements(relationObject, TREE_PATH, (Resource) null).nextStatement().toString());
		assertEquals(
				String.format("[%s, https://w3id.org/tree#node, http://localhost:8080/mobility-hindrances/node]",
						relationObject),
				model.listStatements(relationObject, TREE_NODE, (Resource) null).nextStatement().toString());
		assertEquals(
				String.format("[%s, https://w3id.org/tree#value, \"value\"^^http://www.w3.org/2001/XMLSchema#dateTime]",
						relationObject),
				model.listStatements(relationObject, TREE_VALUE, (Resource) null).nextStatement().toString());
	}

	private void verifyMemberStatements(Model model) {
		assertEquals(
				"[http://localhost:8080/mobility-hindrances, https://w3id.org/tree#member, https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165]",
				model.listStatements(null, TREE_MEMBER, (Resource) null).nextStatement().toString());
	}

	private int getNumberOfStatements(Model model) {
		AtomicInteger statementCounter = new AtomicInteger();
		model.listStatements().forEach((statement) -> statementCounter.getAndIncrement());
		return statementCounter.get();
	}

	private void verifyTreeNodeStatement(Model model) {
		assertEquals(
				"[" + HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME
						+ ", http://www.w3.org/1999/02/22-rdf-syntax-ns#type, https://w3id.org/tree#Node]",
				model.listStatements(null, RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE)).nextStatement()
						.toString());
	}

	private void verifyIsViewOfStatement(Model model) {
		assertEquals(
				"[" + HOST_NAME + "/" + COLLECTION_NAME
						+ ", https://w3id.org/tree#view, " + HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME + "]",
				model.listStatements(null, TREE_VIEW, (Resource) null).nextStatement()
						.toString());
	}

	private void verifyIsPartOfStatement(Model model) {
		assertEquals(
				"[" + HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME
						+ ", http://purl.org/dc/terms/isPartOf, " + HOST_NAME + "/" + COLLECTION_NAME + "]",
				model.listStatements(null, IS_PART_OF_PROPERTY, (Resource) null).nextStatement()
						.toString());
	}

}