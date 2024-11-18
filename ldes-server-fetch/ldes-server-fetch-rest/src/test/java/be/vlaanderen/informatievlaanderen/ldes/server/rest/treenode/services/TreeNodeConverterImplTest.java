package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.HostNamePrefixConstructor;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.TreeRelation;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.vocabulary.RDF;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.assertj.core.api.Assertions.*;

class TreeNodeConverterImplTest {

	private static final String HOST_NAME = "http://localhost:8080";
	private static final String COLLECTION_NAME = "mobility-hindrances";
	public static final Resource COLLECTION_URI = createResource(HOST_NAME + "/" + COLLECTION_NAME);
	private static final String PREFIX = HOST_NAME + "/" + COLLECTION_NAME + "/";
	private static final String VIEW_NAME = "view";
	private static final Resource VIEW_URI = createResource(HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME);
	private final PrefixAdder prefixAdder = new PrefixAdderImpl(List.of());
	private final HostNamePrefixConstructor prefixConstructor = new HostNamePrefixConstructor(HOST_NAME);
	private final TreeNodeStatementCreatorImpl treeNodeStatementCreator = new TreeNodeStatementCreatorImpl();
	private TreeNodeConverterImpl treeNodeConverter;

	@BeforeEach
	void setUp() {
		Model shacl = RDFParser.source("eventstream/streams/example-shape.ttl").lang(Lang.TURTLE).build().toModel();

		EventStream eventStream = new EventStream(COLLECTION_NAME,
				"http://www.w3.org/ns/prov#generatedAtTime",
				"http://purl.org/dc/terms/isVersionOf", null);

		treeNodeConverter = new TreeNodeConverterImpl(prefixAdder, prefixConstructor, treeNodeStatementCreator);
		treeNodeStatementCreator.handleEventStreamInitEvent(new EventStreamCreatedEvent(eventStream));
		treeNodeStatementCreator.handleShaclInitEvent(new ShaclChangedEvent(COLLECTION_NAME, shacl));
	}

	@Test
	void when_TreeNodeHasNoMembersAndIsAView_ModelHasTreeNodeAndLdesStatements() {
		TreeNode treeNode = new TreeNode("/" + COLLECTION_NAME + "/" + VIEW_NAME, false, true, List.of(), List.of(),
				COLLECTION_NAME, null);
		ViewName viewName = new ViewName(COLLECTION_NAME, VIEW_NAME);
		Model dcat = RDFParser.source("eventstream/streams/dcat-view-valid.ttl").lang(Lang.TURTLE).build().toModel();
		DcatView dcatView = DcatView.from(viewName, dcat);
		treeNodeStatementCreator.handleDcatViewSavedEvent(new DcatViewSavedEvent(dcatView));

		Model model = treeNodeConverter.toModel(treeNode);

		assertThat(model)
				.has(size(25))
				.has(treeNodeStatement())
				.has(ldesStatements());
	}

	@Test
	void when_TreeNodeHasNoMembersAndIsNotAView_ModelHasTreeNodeAndPartOfStatements() {
		TreeNode treeNode = new TreeNode("/" + COLLECTION_NAME + "/" + VIEW_NAME, false, false, List.of(), List.of(),
				COLLECTION_NAME, null);

		Model model = treeNodeConverter.toModel(treeNode);

		assertThat(model)
				.has(size(3))
				.has(treeNodeStatement())
				.has(isPartOfStatement())
				.has(not(remainingItemsStatement()));
	}

	@Test
	void when_TreeNodeHasMembersAndARelations_ModelHasMultipleStatements() {
		Model ldesMemberModel = RDFParserBuilder.create().fromString("""
				<http://localhost:8080/mobility-hindrances> <https://w3id.org/tree#member>
				<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165>
				.""").lang(Lang.NQUADS).toModel();
		String memberUri = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165";
		Member member = new Member(
				memberUri, ldesMemberModel);
		TreeRelation treeRelation = new TreeRelation("path",
				new LdesFragmentIdentifier("mobility-hindrances/node", List.of()), "value",
				"http://www.w3.org/2001/XMLSchema#dateTime", "relation");
		TreeNode treeNode = new TreeNode("/" + COLLECTION_NAME + "/" + VIEW_NAME, false, false, List.of(treeRelation),
				List.of(member), COLLECTION_NAME, null);

		Model model = treeNodeConverter.toModel(treeNode);

		assertThat(model)
				.has(size(9))
				.has(relationStatements(model.listObjectsOfProperty(TREE_RELATION).nextNode().asResource()))
				.has(treeNodeStatement())
				.has(isPartOfStatement())
				.has(statement(COLLECTION_URI, TREE_MEMBER, createResource(memberUri)))
				.has(not(remainingItemsStatement()));
	}

	private Condition<Model> ldesStatements() {
		return allOf(
				statement(COLLECTION_URI, RDF.type, createResource(LDES_EVENT_STREAM_URI)),
				statement(COLLECTION_URI, LDES_TIMESTAMP_PATH, createResource("http://www.w3.org/ns/prov#generatedAtTime")),
				statement(COLLECTION_URI, LDES_VERSION_OF, createResource("http://purl.org/dc/terms/isVersionOf")),
				statementsWithSubjectCountedTimes("http://localhost:8080/mobility-hindrances/view/description", 8),
				statementsWithSubjectCountedTimes("http://localhost:8080/collectionName1/shape", 3),
				statement(COLLECTION_URI, TREE_VIEW, VIEW_URI));
	}

	private Condition<Model> statementsWithSubjectCountedTimes(String subject, int expectedCount) {
		return new Condition<>(
				model -> model.listObjectsOfProperty(createResource(subject), null).toList().size() == expectedCount,
				"TreeNode must have %d statements with subject %s", expectedCount, subject
		);
	}

	private Condition<Model> relationStatements(Resource relationObject) {
		Model expectedModel = ModelFactory.createDefaultModel()
				.add(VIEW_URI, TREE_RELATION, relationObject)
				.add(relationObject, RDF.type, createResource("relation"))
				.add(relationObject, TREE_PATH, createProperty("path"))
				.add(relationObject, TREE_NODE, createResource(HOST_NAME + "/" + COLLECTION_NAME + "/node"))
				.add(relationObject, TREE_VALUE, "value", XSDDatatype.XSDdateTime);
		return new Condition<>(model -> model.containsAll(expectedModel), "TreeNode must have expected relation statements");
	}

	private Condition<Model> treeNodeStatement() {
		return statement(VIEW_URI, RDF.type, createResource(TREE_NODE_RESOURCE));
	}

	public Condition<Model> isPartOfStatement() {
		return statement(VIEW_URI, IS_PART_OF_PROPERTY, COLLECTION_URI);
	}

	public Condition<Model> statement(Resource subject, Property predicate, RDFNode object) {
		return new Condition<>(
				model -> model.contains(subject, predicate, object),
				"TreeNode must have a statement [%s, %s, %s]", subject, predicate, object
		);
	}

	private Condition<Model> remainingItemsStatement() {
		return new Condition<>(
				model -> model.listStatements(null, createProperty(TREE_REMAINING_ITEMS), (Resource) null).hasNext(),
				"TreeNode must have a remainingItems statement"
		);
	}

	private Condition<Model> size(int size) {
		return new Condition<>(model -> model.size() == size, "Model must have %d statements", size);
	}

	@Test
	void testHandleDcatViewEvents() {
		final TreeNode treeNode = new TreeNode("/" + COLLECTION_NAME + "/" + VIEW_NAME, false, true, List.of(), List.of(),
				COLLECTION_NAME, null);
		final ViewName viewName = new ViewName(COLLECTION_NAME, VIEW_NAME);
		final Model dcat = RDFParser.source("eventstream/streams/dcat-view-valid.ttl").lang(Lang.TURTLE).build().toModel();
		final DcatView dcatView = DcatView.from(viewName, dcat);

		assertThat(treeNodeConverter.toModel(treeNode).size()).isEqualTo(11);
		treeNodeStatementCreator.handleDcatViewSavedEvent(new DcatViewSavedEvent(dcatView));
		assertThat(treeNodeConverter.toModel(treeNode).size()).isEqualTo(25);
		treeNodeStatementCreator.handleDcatViewDeletedEvent(new DcatViewDeletedEvent(dcatView.getViewName()));
		assertThat(treeNodeConverter.toModel(treeNode).size()).isEqualTo(11);
	}

	@Test
	void test_HandleEventStreamDeleted() {
		final TreeNode treeNode = new TreeNode(PREFIX + VIEW_NAME, false, true, List.of(), List.of(),
				COLLECTION_NAME, null);

		treeNodeStatementCreator.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(COLLECTION_NAME));

		assertThatThrownBy(() -> treeNodeConverter.toModel(treeNode)).isInstanceOf(MissingResourceException.class);
	}
}
