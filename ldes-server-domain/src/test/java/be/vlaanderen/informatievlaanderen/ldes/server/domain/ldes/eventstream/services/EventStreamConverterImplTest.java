package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
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

class EventStreamConverterImplTest {

	private final PrefixAdder prefixAdder = new PrefixAdderImpl();
	public static final String DATE_TIME_TYPE = "http://www.w3.org/2001/XMLSchema#dateTime";
	private EventStreamConverter eventStreamConverter;
	private static final String BASE_URL = "http://localhost:8080/mobility-hindrances";

	@BeforeEach
	void setUp() {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setHostName("http://localhost:8080");
		ldesConfig.setCollectionName("mobility-hindrances");
		ldesConfig.validation()
				.setShape("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape");
		ldesConfig.setMemberType("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder");
		ldesConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
		ldesConfig.setVersionOf("http://purl.org/dc/terms/isVersionOf");

		Model dcat = RDFParserBuilder.create().fromString("""
				<http://localhost:8080/metadata/mobility-hindrances> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
				<http://www.w3.org/ns/dcat#Catalog>
				.""").lang(Lang.NQUADS).toModel();
		ldesConfig.setDcat(dcat);

		eventStreamConverter = new EventStreamConverterImpl(prefixAdder, ldesConfig);
	}

	@Test
	void when_LdesFragmentHasTwoViews_ModelHasNineStatement() {
		EventStream eventStream = new EventStream("mobility-hindrances", "http://www.w3.org/ns/prov#generatedAtTime",
				"http://purl.org/dc/terms/isVersionOf",
				"https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape",
				List.of(createView("view1", List.of()), createView("view2",
						List.of(new TreeRelation("path", "/node", "value", DATE_TIME_TYPE, "relation")))));

		Model model = eventStreamConverter.toModel(eventStream);

		assertEquals(14, getNumberOfStatements(model));
		assertEquals(
				"[" + BASE_URL
						+ ", http://www.w3.org/1999/02/22-rdf-syntax-ns#type, https://w3id.org/ldes#EventStream]",
				model.listStatements(createResource(BASE_URL), RDF_SYNTAX_TYPE, (Resource) null).nextStatement()
						.toString());
		assertEquals(
				"[" + BASE_URL + ", https://w3id.org/ldes#timestampPath, http://www.w3.org/ns/prov#generatedAtTime]",
				model.listStatements(createResource(BASE_URL), LDES_TIMESTAMP_PATH, (Resource) null).nextStatement()
						.toString());
		assertEquals("[" + BASE_URL + ", https://w3id.org/ldes#versionOfPath, http://purl.org/dc/terms/isVersionOf]",
				model.listStatements(createResource(BASE_URL), LDES_VERSION_OF, (Resource) null).nextStatement()
						.toString());
		assertEquals("[" + BASE_URL
				+ ", https://w3id.org/tree#shape, https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape]",
				model.listStatements(createResource(BASE_URL), TREE_SHAPE, (Resource) null).nextStatement().toString());
		assertEquals("[" + BASE_URL + ", https://w3id.org/tree#view, " + BASE_URL + "/view1]",
				model.listStatements(createResource(BASE_URL), TREE_VIEW, createResource(BASE_URL + "/view1"))
						.nextStatement()
						.toString());
		assertEquals("[" + BASE_URL + ", https://w3id.org/tree#view, " + BASE_URL + "/view2]",
				model.listStatements(createResource(BASE_URL), TREE_VIEW, createResource(BASE_URL + "/view2"))
						.nextStatement()
						.toString());
		assertEquals(
				"[" + BASE_URL + "/view1"
						+ ", http://www.w3.org/1999/02/22-rdf-syntax-ns#type, https://w3id.org/tree#Node]",
				model.listStatements(createResource(BASE_URL + "/view1"), RDF_SYNTAX_TYPE, (Resource) null)
						.nextStatement()
						.toString());
		assertEquals(
				"[" + BASE_URL + "/view2"
						+ ", http://www.w3.org/1999/02/22-rdf-syntax-ns#type, https://w3id.org/tree#Node]",
				model.listStatements(createResource(BASE_URL + "/view2"), RDF_SYNTAX_TYPE, (Resource) null)
						.nextStatement()
						.toString());
		assertEquals(
				"[http://localhost:8080/metadata/mobility-hindrances, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://www.w3.org/ns/dcat#Catalog]",
				model.listStatements(createResource("http://localhost:8080/metadata/mobility-hindrances"),
						RDF_SYNTAX_TYPE, (Resource) null).nextStatement()
						.toString());
		Resource relationObject = model.listStatements(null, TREE_RELATION,
				(Resource) null).nextStatement().getObject()
				.asResource();
		verifyRelationStatements(model, relationObject);
	}

	private void verifyRelationStatements(Model model, Resource relationObject) {
		assertEquals(
				String.format(
						"[" + BASE_URL + "/view2"
								+ ", https://w3id.org/tree#relation, %s]",
						relationObject),
				model.listStatements(createResource(BASE_URL + "/view2"), TREE_RELATION,
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

	private int getNumberOfStatements(Model model) {
		AtomicInteger statementCounter = new AtomicInteger();
		model.listStatements().forEach((statement) -> statementCounter.getAndIncrement());
		return statementCounter.get();
	}

	private TreeNode createView(String viewName, List<TreeRelation> relations) {
		return new TreeNode("http://localhost:8080/mobility-hindrances/" + viewName, false, false, true, relations,
				List.of());
	}
}
