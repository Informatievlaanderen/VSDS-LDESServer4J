package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatserver.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatserver.entity.DcatCatalogEntity;
import org.apache.jena.rdf.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DcatCatalogEntityConverterTest {
	private static final String ID = "id";
	private static DcatServer dcatServer;
	private final DcatCatalogEntityConverter converter = new DcatCatalogEntityConverter();

	@BeforeAll
	static void beforeAll() {
		Resource subject = ResourceFactory.createResource("http://localhost:8080/dcat/id");
		Resource object = ResourceFactory.createProperty("http://www.w3.org/ns/dcat#", "Catalog");
		Statement statement = ResourceFactory.createStatement(subject, RdfConstants.RDF_SYNTAX_TYPE, object);
		Model dcat = ModelFactory.createDefaultModel().add(statement);
		dcatServer = new DcatServer(ID, dcat);
	}

	@Test
	void test_conversionFromAndToDomain() {
		final DcatCatalogEntity entity = converter.fromDcatServer(dcatServer);
		final DcatServer converted = converter.toDcatServer(entity);

		assertEquals(dcatServer, converted);
		assertEquals(dcatServer.getId(), entity.getId());
		assertTrue(entity.getDcat().contains("<http://localhost:8080/dcat/id>"));
	}
}