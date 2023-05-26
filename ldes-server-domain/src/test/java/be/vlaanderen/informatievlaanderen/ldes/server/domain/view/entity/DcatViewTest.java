package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView.DCAT_DATA_SERVICE;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DcatViewTest {

	private final static String COLLECTION_NAME = "collectionName";
	private final static String VIEW = "view";
	private final static ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, VIEW);
	private final static Model MODEL = ModelFactory.createDefaultModel();

	@Test
	void test_GetViewName() {
		assertEquals(VIEW_NAME, DcatView.from(VIEW_NAME, MODEL).getViewName());
	}

	@Test
	void test_GetDcat() {
		assertEquals(MODEL, DcatView.from(VIEW_NAME, MODEL).getDcat());
	}

	@Test
	void should_ReturnNamedDcatStatements_when_GetStatementsWithBaseIsCalled() {
		String host = "http://localhost.dev";
		Model anon = RDFParser.source("viewconverter/dcat-view-valid.ttl").lang(Lang.TURTLE).build().toModel();
		DcatView dcatView = DcatView.from(VIEW_NAME, anon);

		List<Statement> result = dcatView.getStatementsWithBase(host);

		assertEquals(anon.listStatements().toList().size(), result.size());
		Model resultModel = ModelFactory.createDefaultModel();
		resultModel.add(result);
		Resource iri = ResourceFactory.createResource(host + "/" + COLLECTION_NAME + "/" + VIEW + "/description");
		assertEquals(4, resultModel.listStatements(iri, null, (RDFNode) null).toList().size());
		assertEquals(DCAT_DATA_SERVICE, resultModel.listObjectsOfProperty(iri, RDF.type).next());
		assertTrue(resultModel.listObjectsOfProperty(createProperty("http://purl.org/dc/terms/license")).hasNext());
		assertEquals("Geospatial fragmentation for my LDES",
				resultModel.listObjectsOfProperty(iri, createProperty("http://purl.org/dc/terms/description")).next()
						.asLiteral().getString());
		assertEquals("My geo-spatial view",
				resultModel.listObjectsOfProperty(iri, createProperty("http://purl.org/dc/terms/title")).next()
						.asLiteral().getString());
	}

	@ParameterizedTest
	@ArgumentsSource(EqualityTestProvider.class)
	void testEqualsAndHashCode(BiConsumer<Object, Object> assertion, DcatView a, DcatView b) {
		assertNotNull(assertion);
		assertion.accept(a, b);
		if (a != null && b != null) {
			assertion.accept(a.hashCode(), b.hashCode());
		}
	}

	static class EqualityTestProvider implements ArgumentsProvider {

		private static final Model modelB = ModelFactory.createDefaultModel();
		static {
			modelB.add(ResourceFactory.createResource("http://example.org"), RDF.type, "type");
		}

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(equals(), DcatView.from(VIEW_NAME, MODEL), DcatView.from(VIEW_NAME, MODEL)),
					Arguments.of(equals(), DcatView.from(VIEW_NAME, MODEL), DcatView.from(VIEW_NAME, modelB)),
					Arguments.of(equals(), DcatView.from(VIEW_NAME, MODEL), DcatView.from(VIEW_NAME, modelB)),
					Arguments.of(notEquals(), DcatView.from(new ViewName("otherCollection", VIEW), MODEL),
							DcatView.from(VIEW_NAME, modelB)));
		}

		private static BiConsumer<Object, Object> equals() {
			return Assertions::assertEquals;
		}

		private static BiConsumer<Object, Object> notEquals() {
			return Assertions::assertNotEquals;
		}

	}

}