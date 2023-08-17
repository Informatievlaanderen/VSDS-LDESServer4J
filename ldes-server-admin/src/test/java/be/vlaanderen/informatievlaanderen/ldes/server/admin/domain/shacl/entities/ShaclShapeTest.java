package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ResourceFactory.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ShaclShapeTest {
	private static final String COLLECTION = "my-collection";
	private static final ShaclShape SHACL_SHAPE = new ShaclShape(COLLECTION, ShaclShapeArgumentsProvider.getModel());

	@Test
	void test_equality() {
		ShaclShape other = new ShaclShape(COLLECTION, ShaclShapeArgumentsProvider.getModel());

		assertEquals(SHACL_SHAPE, other);
		assertEquals(SHACL_SHAPE.hashCode(), other.hashCode());
	}

	@ParameterizedTest
	@ArgumentsSource(ShaclShapeArgumentsProvider.class)
	void test_inequality(Object other) {
		assertNotEquals(SHACL_SHAPE, other);
		if (other != null) {
			assertNotEquals(SHACL_SHAPE.hashCode(), other.hashCode());
		}
	}

	static class ShaclShapeArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					Arguments.of(new ShaclShape("other collection", getModel())),
					Arguments.of((Object) null),
					Arguments.of("String instead of shacl"));
		}

		public static Model getModel() {
			Model model = ModelFactory.createDefaultModel();
			model.add(createStatement(
					createResource("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464"),
					createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
					createResource("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder")));
			return model;
		}
	}
}
