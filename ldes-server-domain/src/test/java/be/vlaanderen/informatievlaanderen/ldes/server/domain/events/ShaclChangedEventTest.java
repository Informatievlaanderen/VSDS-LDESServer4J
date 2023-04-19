package be.vlaanderen.informatievlaanderen.ldes.server.domain.events;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ResourceFactory.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.*;

class ShaclChangedEventTest {

	private static final String COLLECTION_NAME = "collectionName";

	@Test
	void test_getters() {
		ShaclChangedEvent shaclChangedEvent = new ShaclChangedEvent(COLLECTION_NAME,
				ShaclChangedEventArgumentsProvider.getModel());

		assertEquals(COLLECTION_NAME, shaclChangedEvent.getCollectionName());
		assertTrue(ShaclChangedEventArgumentsProvider.getModel().isIsomorphicWith(shaclChangedEvent.getShacl()));
	}

	@Test
	void test_equality() {
		ShaclChangedEvent shaclChangedEvent = new ShaclChangedEvent(COLLECTION_NAME,
				ShaclChangedEventArgumentsProvider.getModel());
		ShaclChangedEvent otherShaclChangedEvent = new ShaclChangedEvent(COLLECTION_NAME,
				ShaclChangedEventArgumentsProvider.getModel());
		assertEquals(shaclChangedEvent, otherShaclChangedEvent);
		assertEquals(shaclChangedEvent, shaclChangedEvent);
		assertEquals(otherShaclChangedEvent, otherShaclChangedEvent);
	}

	@ParameterizedTest
	@ArgumentsSource(ShaclChangedEventArgumentsProvider.class)
	void test_inequality(Object otherShaclChangedEvent) {
		ShaclChangedEvent shaclChangedEvent = new ShaclChangedEvent(COLLECTION_NAME,
				ShaclChangedEventArgumentsProvider.getModel());
		assertNotEquals(shaclChangedEvent, otherShaclChangedEvent);
	}

	static class ShaclChangedEventArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(new ShaclChangedEvent("otherCollectionName", getModel())),
					Arguments.of(new ShaclChangedEvent(COLLECTION_NAME, getOtherModel())),
					Arguments.of(new BigDecimal(1)));
		}

		public static Model getModel() {
			Model model = ModelFactory.createDefaultModel();
			model.add(createStatement(
					createResource("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464"),
					createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
					createResource("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder")));
			return model;
		}

		public static Model getOtherModel() {
			Model model = ModelFactory.createDefaultModel();
			model.add(createStatement(
					createResource("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810463"),
					createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
					createResource("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder")));
			return model;
		}
	}

}