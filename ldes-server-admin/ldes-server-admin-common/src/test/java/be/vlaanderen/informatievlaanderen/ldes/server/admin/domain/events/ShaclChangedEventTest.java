package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.events;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ShaclChangedEvent;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShaclChangedEventTest {

	private static final String COLLECTION_NAME = "collectionName";

	@Test
	void test_getters() {
		ShaclChangedEvent shaclChangedEvent = new ShaclChangedEvent(COLLECTION_NAME,
				ShaclChangedEventArgumentsProvider.getModel());

		assertEquals(COLLECTION_NAME, shaclChangedEvent.getCollection());
		assertTrue(ShaclChangedEventArgumentsProvider.getModel()
				.isIsomorphicWith(shaclChangedEvent.getModel()));
	}

	@Test
	void test_equality() {
		ShaclChangedEvent shaclChangedEvent = new ShaclChangedEvent(COLLECTION_NAME,
				ShaclChangedEventArgumentsProvider.getModel());
		ShaclShape otherShaclShape = new ShaclShape(COLLECTION_NAME, ShaclChangedEventArgumentsProvider.getModel());
		ShaclChangedEvent otherShaclChangedEvent = new ShaclChangedEvent(otherShaclShape.getCollection(),
				otherShaclShape.getModel());
		assertEquals(shaclChangedEvent, otherShaclChangedEvent);
		assertEquals(shaclChangedEvent, shaclChangedEvent);
		assertEquals(otherShaclChangedEvent, otherShaclChangedEvent);
	}

	@ParameterizedTest
	@ArgumentsSource(ShaclChangedEventArgumentsProvider.class)
	void test_inequality(Object otherShaclChangedEvent) {
		ShaclChangedEvent shaclChangedEvent = new ShaclChangedEvent(COLLECTION_NAME,
				ShaclChangedEventArgumentsProvider.getModel());
		Assertions.assertNotEquals(shaclChangedEvent, otherShaclChangedEvent);
	}

	static class ShaclChangedEventArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(new ShaclChangedEvent("otherCollectionName", getModel())),
					Arguments.of(new BigDecimal(1)));
		}

		public static Model getModel() {
			Model model = ModelFactory.createDefaultModel();
			model.add(ResourceFactory.createStatement(
					ResourceFactory.createResource("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464"),
					ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
					ResourceFactory.createResource("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder")));
			return model;
		}

		public static Model getOtherModel() {
			Model model = ModelFactory.createDefaultModel();
			model.add(ResourceFactory.createStatement(
					ResourceFactory.createResource("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810463"),
					ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
					ResourceFactory.createResource("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder")));
			return model;
		}
	}

}