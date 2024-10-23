package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
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

import static org.assertj.core.api.Assertions.assertThat;

class DcatViewTest {

	private static final String COLLECTION_NAME = "collectionName";
	private static final String VIEW = "view";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, VIEW);
	private static final Model MODEL = ModelFactory.createDefaultModel();

	@Test
	void test_GetViewName() {
		Assertions.assertEquals(VIEW_NAME, DcatView.from(VIEW_NAME, MODEL).getViewName());
	}

	@Test
	void test_GetDcat() {
		Assertions.assertEquals(MODEL, DcatView.from(VIEW_NAME, MODEL).getDcat());
	}

	@Test
	void should_ReturnNamedDcatStatements_when_GetStatementsWithBaseIsCalled() {
		final int nrOfAdditionalDcatStatements = 6; // servesDataset, 2 x endpointURL + identifier + 2 x endpointDescription
		final String host = "http://localhost.dev";
		final Model anon = RDFParser.source("dcat/dataservice/dcat-view-valid.ttl").lang(Lang.TURTLE).build().toModel();
		final Resource iri = ResourceFactory.createResource(host + "/" + COLLECTION_NAME + "/" + VIEW + "/description");

		final DcatView dcatView = DcatView.from(VIEW_NAME, anon);

		final List<Statement> result = dcatView.getStatementsWithBase(host);
		final Model resultModel = ModelFactory.createDefaultModel().add(result);

		assertThat(result).hasSize(anon.listStatements().toList().size() + nrOfAdditionalDcatStatements);
		assertThat(resultModel.listStatements(iri, null, (RDFNode) null).toList()).hasSize(8);
		assertThat(resultModel.listObjectsOfProperty(iri, RDF.type).next()).isEqualTo(DcatView.DCAT_DATA_SERVICE);
		assertThat(resultModel.listObjectsOfProperty(ResourceFactory.createProperty("http://purl.org/dc/terms/license"))).hasNext();
		assertThat(resultModel.listObjectsOfProperty(iri, ResourceFactory.createProperty("http://purl.org/dc/terms/description")).next().asLiteral().getString())
				.isEqualTo("Geospatial fragmentation for my LDES");
		assertThat(resultModel.listObjectsOfProperty(iri, ResourceFactory.createProperty("http://purl.org/dc/terms/title")).next()
				.asLiteral().getString()).isEqualTo("My geo-spatial view");
		assertThat(resultModel.listObjectsOfProperty(iri, DcatView.DCAT_ENDPOINT_URL).next().asResource().getURI())
				.isEqualTo("http://localhost.dev/collectionName/view");
		assertThat(resultModel.listObjectsOfProperty(iri, DcatView.DCAT_SERVES_DATASET).next().asResource().getURI())
				.isEqualTo("http://localhost.dev/collectionName");
	}

	@ParameterizedTest
	@ArgumentsSource(EqualityTestProvider.class)
	void testEqualsAndHashCode(BiConsumer<Object, Object> assertion, DcatView a, DcatView b) {
		Assertions.assertNotNull(assertion);
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

	@Test
	void when_GetViewDescriptionResourceIsCalled_should_ReturnValidResource() {
		DcatView dcatView = DcatView.from(VIEW_NAME, MODEL);
		String host = "http://localhost.dev";

		Resource result = dcatView.getViewDescriptionResource(host);

		Assertions.assertEquals("http://localhost.dev/collectionName/view/description", result.getURI());
	}
}