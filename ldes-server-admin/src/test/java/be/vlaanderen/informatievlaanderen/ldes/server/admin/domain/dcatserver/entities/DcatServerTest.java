package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcatserver.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities.DcatServer.DCAT_CATALOG;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.junit.jupiter.api.Assertions.*;

class DcatServerTest {
	private static final String ID = "id";
	private static final Model DCAT = initDcat();
	private static final DcatServer SERVER_DCAT = new DcatServer(ID, DCAT);

	@Test
	void test_equality() {
		DcatServer other = new DcatServer(ID, DCAT);
		DcatServer other2 = new DcatServer(ID, ModelFactory.createDefaultModel());

		assertEquals(SERVER_DCAT, SERVER_DCAT);
		assertEquals(other, other);
		assertEquals(SERVER_DCAT, other);
		assertEquals(SERVER_DCAT, other2);
		assertEquals(other, other2);

		assertEquals(SERVER_DCAT.hashCode(), other.hashCode());
		assertEquals(SERVER_DCAT.hashCode(), other2.hashCode());
	}

	@ParameterizedTest
	@ArgumentsSource(ServerDcatArgumentsProvider.class)
	void test_inequality(Object other) {
		assertNotEquals(SERVER_DCAT, other);
		if (other != null) {
			assertNotEquals(SERVER_DCAT.hashCode(), other.hashCode());
		}
	}

	private static Model initDcat() {
		Resource subject = ResourceFactory.createResource();
		Resource object = ResourceFactory.createProperty("http://www.w3.org/ns/dcat#", "Catalog");
		Statement statement = ResourceFactory.createStatement(subject, RdfConstants.RDF_SYNTAX_TYPE, object);
		return ModelFactory.createDefaultModel().add(statement);
	}

	static class ServerDcatArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					new DcatServer("other", DCAT),
					new DcatServer("fake", ModelFactory.createDefaultModel()),
					null, "Not a dcat").map(Arguments::of);
		}
	}

	@Test
	void when_GetViewDescriptionResourceIsCalled_should_ReturnValidResource() {
		DcatServer dcatServer = new DcatServer(ID, DCAT);
		String host = "http://localhost.dev";

		Resource result = dcatServer.getServerResource(host);

		assertEquals("http://localhost.dev", result.getURI());
	}

	@Test
	void should_ReturnNamedDcatStatements_when_GetStatementsWithBaseIsCalled() {
		String host = "http://localhost.dev";
		Model view1 = RDFParser.source("viewconverter/dcat-view-valid.ttl").lang(Lang.TURTLE).build().toModel();
		Model view2 = ModelFactory.createDefaultModel();
		DcatView dcatView1 = DcatView.from(new ViewName("col1", "view1"), view1);
		DcatView dcatView2 = DcatView.from(new ViewName("col1", "view2"), view2);
		DcatServer dcatServer = new DcatServer(ID, DCAT);
		DcatDataset dcatDataset = new DcatDataset("col1");

		List<Statement> result = dcatServer.getStatementsWithBase(host, List.of(dcatView1, dcatView2),
				List.of(dcatDataset));

		assertEquals(4, result.size()); // 1 from catalog + 2 from views
		Model resultModel = ModelFactory.createDefaultModel();
		resultModel.add(result);
		Resource iri = ResourceFactory.createResource(host);
		assertEquals(4, resultModel.listStatements(iri, null, (RDFNode) null).toList().size());
		assertEquals(DCAT_CATALOG, resultModel.listObjectsOfProperty(iri, RDF.type).next());
		assertDataserviceStatements(resultModel, iri);
		assertDatasetStatements(resultModel, iri);
	}

	private void assertDataserviceStatements(Model resultModel, Resource iri) {
		List<String> dataServiceUris = resultModel
				.listObjectsOfProperty(iri, createProperty("http://www.w3.org/ns/dcat#service"))
				.mapWith(RDFNode::asResource).mapWith(Resource::getURI).toList();
		assertEquals(2, dataServiceUris.size());
		assertTrue(dataServiceUris.contains("http://localhost.dev/col1/view1/description"));
		assertTrue(dataServiceUris.contains("http://localhost.dev/col1/view2/description"));
	}

	private void assertDatasetStatements(Model resultModel, Resource iri) {
		List<String> dataServiceUris = resultModel
				.listObjectsOfProperty(iri, createProperty("http://www.w3.org/ns/dcat#dataset"))
				.mapWith(RDFNode::asResource).mapWith(Resource::getURI).toList();
		assertEquals(1, dataServiceUris.size());
		assertTrue(dataServiceUris.contains("http://localhost.dev/col1"));
	}

}
