package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import org.apache.jena.rdf.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
		Resource subject = ResourceFactory.createResource("http://localhost:8080/dcat/id");
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

}