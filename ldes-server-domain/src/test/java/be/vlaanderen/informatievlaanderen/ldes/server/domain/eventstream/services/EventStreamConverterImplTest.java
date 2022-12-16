package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_TIMESTAMP_PATH;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES_VERSION_OF;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_SHAPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_VIEW;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;

class EventStreamConverterImplTest {

	private final PrefixAdder prefixAdder = new PrefixAdderImpl();
	private EventStreamConverter eventStreamConverter;

	@BeforeEach
	void setUp() {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setHostName("http://localhost:8080");
		ldesConfig.setCollectionName("mobility-hindrances");
		ldesConfig.setShape("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape");
		ldesConfig.setMemberType("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder");
		ldesConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
		ldesConfig.setVersionOf("http://purl.org/dc/terms/isVersionOf");
		eventStreamConverter = new EventStreamConverterImpl(prefixAdder, ldesConfig);
	}

	@Test
	void when_LdesFragmentHasTwoViews_ModelHasSixStatement() {
		EventStream eventStream = new EventStream("mobility-hindrances", "http://www.w3.org/ns/prov#generatedAtTime",
				"http://purl.org/dc/terms/isVersionOf",
				"https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape",
				List.of("view1", "view2"));

		Model model = eventStreamConverter.toModel(eventStream);

		String id = "http://localhost:8080/mobility-hindrances";

		assertEquals(6, getNumberOfStatements(model));
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
		assertEquals("[" + id + ", https://w3id.org/tree#view, " + id + "/view1]",
				model.listStatements(createResource(id), TREE_VIEW, createResource(id + "/view1")).nextStatement()
						.toString());
		assertEquals("[" + id + ", https://w3id.org/tree#view, " + id + "/view2]",
				model.listStatements(createResource(id), TREE_VIEW, createResource(id + "/view2")).nextStatement()
						.toString());
	}

	private int getNumberOfStatements(Model model) {
		AtomicInteger statementCounter = new AtomicInteger();
		model.listStatements().forEach((statement) -> statementCounter.getAndIncrement());
		return statementCounter.get();
	}
}