package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.shaclshape.entity.ShaclShapeEntity;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShaclShapeEntityConverterTest {
	private static final String COLLECTION = "collection_name1";
	private final ShaclShapeEntityConverter converter = new ShaclShapeEntityConverter();

	@Test
	void test_conversionFromAndToDomain() {
		final ShaclShape shaclShape = new ShaclShape(COLLECTION, ModelFactory.createDefaultModel());

		final ShaclShapeEntity shaclShapeEntity = converter.fromShaclShape(shaclShape);
		final ShaclShape convertedShaclShape = converter.toShaclShape(shaclShapeEntity);

		assertEquals(shaclShape.getCollection(), convertedShaclShape.getCollection());
		assertEquals("", shaclShapeEntity.getModel());
		assertTrue(shaclShape.getModel().isIsomorphicWith(convertedShaclShape.getModel()));
	}
}