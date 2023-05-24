package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.entities.ServerDcat;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.serverdcat.entity.ServerDcatEntity;
import org.apache.jena.rdf.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerDcatEntityConverterTest {
	private static final String ID = "id";
	private static ServerDcat serverDcat;
	private final ServerDcatEntityConverter converter = new ServerDcatEntityConverter();

	@BeforeAll
	static void beforeAll() {
		Resource subject = ResourceFactory.createResource("http://localhost:8080/dcat/id");
		Resource object = ResourceFactory.createProperty("http://www.w3.org/ns/dcat#", "Catalog");
		Statement statement = ResourceFactory.createStatement(subject, RdfConstants.RDF_SYNTAX_TYPE, object);
		Model dcat = ModelFactory.createDefaultModel().add(statement);
		serverDcat = new ServerDcat(ID, dcat);
	}

	@Test
	void test_conversionFromAndToDomain() {
		final ServerDcatEntity entity = converter.fromServerDcat(serverDcat);
		final ServerDcat converted = converter.toServerDcat(entity);

		assertEquals(serverDcat, converted);
		assertEquals(serverDcat.getId(), entity.getId());
		assertTrue(entity.getDcat().contains("<http://localhost:8080/dcat/id>"));
	}
}