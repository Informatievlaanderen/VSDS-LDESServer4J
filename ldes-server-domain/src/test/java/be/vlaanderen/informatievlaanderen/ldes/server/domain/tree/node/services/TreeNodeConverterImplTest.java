package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_NODE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_NODE_RESOURCE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_PATH;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_VALUE;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
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
	private static final String VIEW_NAME = "view";
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private static final String FRAGMENT_ID = HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME;
	private static final String TIMESTAMP_PATH = "http://www.w3.org/ns/prov#generatedAtTime";
	public static final String DATE_TIME_TYPE = "http://www.w3.org/2001/XMLSchema#dateTime";
	private final PrefixAdder prefixAdder = new PrefixAdderImpl();
	private TreeNodeConverter treeNodeConverter;

	@BeforeEach
	void setUp() {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setCollectionName(COLLECTION_NAME);
		ldesConfig.setHostName(HOST_NAME);
		ldesConfig.setShape("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape");
		ldesConfig.setMemberType("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder");
		ldesConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
		ldesConfig.setVersionOf("http://purl.org/dc/terms/isVersionOf");
		treeNodeConverter = new TreeNodeConverterImpl( prefixAdder, ldesConfig);
	}

	@Test
	void when_TreeNodeHasNoMembers_ModelHasOneStatement() {
		TreeNode treeNode = new TreeNode("/" + VIEW_NAME, false, false, List.of(), List.of());

		Model model = treeNodeConverter.toModel(treeNode);

		assertEquals(1, getNumberOfStatements(model));
		verifyTreeNodeStatement(model);
	}

	@Test
	void when_TreeNodeHasMembersAndARelations_ModelHasMultipleStatements() {
		Model ldesMemberModel = RDFParserBuilder.create().fromString("""
				<http://localhost:8080/mobility-hindrances> <https://w3id.org/tree#member>
				<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165>
				.""").lang(Lang.NQUADS).toModel();
		Member member = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165", ldesMemberModel,
				List.of());
		TreeRelation treeRelation =new TreeRelation("path", "/node", "value", "http://www.w3.org/2001/XMLSchema#dateTime", "relation");
		TreeNode treeNode = new TreeNode("/" + VIEW_NAME, false, false, List.of(treeRelation), List.of(member));

		Model model = treeNodeConverter.toModel(treeNode);

		assertEquals(8, getNumberOfStatements(model));
		verifyTreeNodeStatement(model);
		Resource relationObject = model.listStatements(null, TREE_RELATION,
				(Resource) null).nextStatement().getObject()
				.asResource();
		verifyRelationStatements(model, relationObject);
		verifyMemberStatements(model);
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

}