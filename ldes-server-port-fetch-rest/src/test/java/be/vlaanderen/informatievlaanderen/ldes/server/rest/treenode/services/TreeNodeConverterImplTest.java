package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DcatViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DcatViewSavedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.assertj.core.api.Assertions.assertThat;

class TreeNodeConverterImplTest {

    private static final String HOST_NAME = "http://localhost:8080";
    private static final String COLLECTION_NAME = "mobility-hindrances";
    private static final String PREFIX = HOST_NAME + "/" + COLLECTION_NAME + "/";
    private static final String VIEW_NAME = "view";
    private final PrefixAdder prefixAdder = new PrefixAdderImpl();
    private final PrefixConstructor prefixConstructor = new PrefixConstructor(HOST_NAME, false);
    private TreeNodeConverterImpl treeNodeConverter;

    @BeforeEach
    void setUp() {
        Model shacl = RDFParser.source("eventstream/streams/example-shape.ttl").lang(Lang.TURTLE).build().toModel();

        EventStream eventStream = new EventStream(COLLECTION_NAME,
                "http://www.w3.org/ns/prov#generatedAtTime",
                "http://purl.org/dc/terms/isVersionOf");

        treeNodeConverter = new TreeNodeConverterImpl(prefixAdder, prefixConstructor);
        treeNodeConverter.handleEventStreamInitEvent(new EventStreamCreatedEvent(eventStream));
        treeNodeConverter.handleShaclInitEvent(new ShaclChangedEvent(COLLECTION_NAME, shacl));
    }

    @Test
    void when_TreeNodeHasNoMembersAndIsAView_ModelHasTreeNodeAndLdesStatements() {
        TreeNode treeNode = new TreeNode(PREFIX + VIEW_NAME, false, true, List.of(), List.of(),
                COLLECTION_NAME);
        ViewName viewName = new ViewName(COLLECTION_NAME, VIEW_NAME);
        Model dcat = RDFParser.source("eventstream/streams/dcat-view-valid.ttl").lang(Lang.TURTLE).build().toModel();
        DcatView dcatView = DcatView.from(viewName, dcat);
        treeNodeConverter.handleDcatViewSavedEvent(new DcatViewSavedEvent(dcatView));

        Model model = treeNodeConverter.toModel(treeNode);

        assertThat(model.listStatements().toList()).hasSize(24);
        verifyTreeNodeStatement(model);
        verifyLdesStatements(model);

        // 04/12/23 Desactivated due to performance issues on the count query
        // refer to: https://github.com/Informatievlaanderen/VSDS-LDESServer4J/issues/1028
//        verifyRemainingItemsStatement(model);
    }

    @Test
    void when_TreeNodeHasNoMembersAndIsNotAView_ModelHasTreeNodeAndPartOfStatements() {
        TreeNode treeNode = new TreeNode(PREFIX + VIEW_NAME, false, false, List.of(), List.of(),
                COLLECTION_NAME);
        Model model = treeNodeConverter.toModel(treeNode);

        assertThat(model.listStatements().toList()).hasSize(2);
        verifyTreeNodeStatement(model);
        verifyIsPartOfStatement(model);
        verifyRemainingItemsStatementAbsent(model);
    }

    @Test
    void when_TreeNodeHasMembersAndARelations_ModelHasMultipleStatements() {
        Model ldesMemberModel = RDFParserBuilder.create().fromString("""
                <http://localhost:8080/mobility-hindrances> <https://w3id.org/tree#member>
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165>
                .""").lang(Lang.NQUADS).toModel();
        Member member = new Member(
                "collectionName/https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165",
                "collectionName",
                0L, ldesMemberModel);
        TreeRelation treeRelation = new TreeRelation("path",
                new LdesFragmentIdentifier("mobility-hindrances/node", List.of()), "value",
                "http://www.w3.org/2001/XMLSchema#dateTime", "relation");
        TreeNode treeNode = new TreeNode(PREFIX + VIEW_NAME, false, false, List.of(treeRelation),
                List.of(member), COLLECTION_NAME);

        Model model = treeNodeConverter.toModel(treeNode);

        assertThat(model.listStatements().toList()).hasSize(9);
        verifyTreeNodeStatement(model);
        verifyIsPartOfStatement(model);
        Resource relationObject = model.listStatements(null, TREE_RELATION,
                        (Resource) null).nextStatement().getObject()
                .asResource();
        verifyRelationStatements(model, relationObject);
        verifyMemberStatements(model);
        verifyRemainingItemsStatementAbsent(model);
    }

    private void verifyLdesStatements(Model model) {
        String id = HOST_NAME + "/" + COLLECTION_NAME;

        assertThat(model.listStatements(createResource(id), RDF_SYNTAX_TYPE, (Resource) null).nextStatement())
                .hasToString(String.format("[%s, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, https://w3id.org/ldes#EventStream]",
                        id));
        assertThat(model.listStatements(createResource(id), LDES_TIMESTAMP_PATH, (Resource) null).nextStatement())
                .hasToString(String.format("[%s, https://w3id.org/ldes#timestampPath, http://www.w3.org/ns/prov#generatedAtTime]",
                        id));
        assertThat(model.listStatements(createResource(id), LDES_VERSION_OF, (Resource) null).nextStatement())
                .hasToString(String.format("[%s, https://w3id.org/ldes#versionOfPath, http://purl.org/dc/terms/isVersionOf]",
                        id));

        verifyIsViewOfStatement(model);
        verifyShaclStatements(model);
        verifyDcatStatements(model);
    }

    private void verifyShaclStatements(Model model) {
        Resource shapeResource = createResource("http://localhost:8080/collectionName1/shape");
        assertThat(model.listStatements(shapeResource, null, (RDFNode) null).toList()).hasSize(3);
    }

    private void verifyDcatStatements(Model model) {
        Resource shapeResource = createResource("http://localhost:8080/mobility-hindrances/view/description");
        assertThat(model.listStatements(shapeResource, null, (RDFNode) null).toList()).hasSize(8);
    }

    private void verifyRelationStatements(Model model, Resource relationObject) {
        assertThat(model.listStatements(createResource(HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME),
                        TREE_RELATION,
                        (Resource) null)
                .nextStatement())
                .hasToString(String.format("[%s, https://w3id.org/tree#relation, %s]",
                        HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME,
                        relationObject));
        assertThat(model.listStatements(relationObject, RDF_SYNTAX_TYPE, (Resource) null).nextStatement())
                .hasToString(String.format("[%s, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, relation]", relationObject));
        assertThat(model.listStatements(relationObject, TREE_PATH, (Resource) null).nextStatement())
                .hasToString(String.format("[%s, https://w3id.org/tree#path, path]", relationObject));
        assertThat(model.listStatements(relationObject, TREE_NODE, (Resource) null).nextStatement())
                .hasToString(String.format("[%s, https://w3id.org/tree#node, http://localhost:8080/mobility-hindrances/node]",
                        relationObject));
        assertThat(model.listStatements(relationObject, TREE_VALUE, (Resource) null).nextStatement())
                .hasToString(String.format("[%s, https://w3id.org/tree#value, \"value\"^^http://www.w3.org/2001/XMLSchema#dateTime]",
                        relationObject));
    }

    private void verifyMemberStatements(Model model) {
        assertThat(model.listStatements(null, TREE_MEMBER, (Resource) null).nextStatement())
                .hasToString("[http://localhost:8080/mobility-hindrances, https://w3id.org/tree#member, https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165]");
    }

    private int getNumberOfStatements(Model model) {
        AtomicInteger statementCounter = new AtomicInteger();
        model.listStatements().forEach((statement) -> statementCounter.getAndIncrement());
        return statementCounter.get();
    }

    private void verifyTreeNodeStatement(Model model) {
        assertThat(model.listStatements(null, RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE)).nextStatement())
                .hasToString(String.format("[%s, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, https://w3id.org/tree#Node]",
                        HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME));
    }

    private void verifyIsViewOfStatement(Model model) {
        assertThat(model.listStatements(null, TREE_VIEW, (Resource) null).nextStatement())
                .hasToString(String.format("[%s, https://w3id.org/tree#view, %s]",
                        HOST_NAME + "/" + COLLECTION_NAME,
                        HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME));
    }

    private void verifyIsPartOfStatement(Model model) {
        assertThat(model.listStatements(null, IS_PART_OF_PROPERTY, (Resource) null).nextStatement())
                .hasToString(String.format("[%s, http://purl.org/dc/terms/isPartOf, %s]",
                        HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME,
                        HOST_NAME + "/" + COLLECTION_NAME));
    }

    private void verifyRemainingItemsStatement(Model model) {
        assertThat(model.listStatements(null, createProperty(TREE_REMAINING_ITEMS), (Resource) null).nextStatement()).hasToString(
                String.format("[%s, %s, \"0\"^^http://www.w3.org/2001/XMLSchema#long]",
                        HOST_NAME + "/" + COLLECTION_NAME + "/" + VIEW_NAME,
                        TREE_REMAINING_ITEMS)
                );
    }

    private void verifyRemainingItemsStatementAbsent(Model model) {
        assertThat(model.listStatements(null, createProperty(TREE_REMAINING_ITEMS), (Resource) null).hasNext()).isFalse();
    }

    @Test
    void testHandleDcatViewEvents() {
        TreeNode treeNode = new TreeNode(PREFIX + VIEW_NAME, false, true, List.of(), List.of(),
                COLLECTION_NAME);
        ViewName viewName = new ViewName(COLLECTION_NAME, VIEW_NAME);
        Model dcat = RDFParser.source("eventstream/streams/dcat-view-valid.ttl").lang(Lang.TURTLE).build().toModel();
        DcatView dcatView = DcatView.from(viewName, dcat);

        assertThat(treeNodeConverter.toModel(treeNode).listStatements().toList()).hasSize(10);
        treeNodeConverter.handleDcatViewSavedEvent(new DcatViewSavedEvent(dcatView));
        assertThat(treeNodeConverter.toModel(treeNode).listStatements().toList()).hasSize(24);
        treeNodeConverter.handleDcatViewDeletedEvent(new DcatViewDeletedEvent(dcatView.getViewName()));
        assertThat(treeNodeConverter.toModel(treeNode).listStatements().toList()).hasSize(10);
    }
}
