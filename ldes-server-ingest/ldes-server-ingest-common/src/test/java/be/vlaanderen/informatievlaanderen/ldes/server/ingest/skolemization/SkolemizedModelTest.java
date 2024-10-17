package be.vlaanderen.informatievlaanderen.ldes.server.ingest.skolemization;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import static be.vlaanderen.informatievlaanderen.ldes.server.ingest.skolemization.SkolemizedModelAssert.assertThatSkolemizedModel;

class SkolemizedModelTest {
	private static final String SKOLEM_URI_TEMPLATE = "http://example.org" + SkolemizedMemberExtractor.SKOLEM_URI + "%s";

	@Test
	void given_ModelWithBNodes_when_getModel_then_NoBNodesArePresent() {
		final Model modelToSkolemize = RDFParser.source("skolemization/activity.nq").toModel();

		final Model skolemizedModel = new SkolemizedModel(SKOLEM_URI_TEMPLATE, modelToSkolemize).getModel();

		assertThatSkolemizedModel(skolemizedModel)
				.hasNoBlankNodes()
				.hasSkolemizedObjectsWithPrefix(1, SKOLEM_URI_TEMPLATE.replace("%s", ""))
				.hasSkolemizedSubjectsWithPrefix(2, SKOLEM_URI_TEMPLATE.replace("%s", ""));
	}

	@Test
	void given_ModelWithoutBNodes_when_getModel_then_ThereAreStillNoBNodesPresent() {
		final Model modelToSkolemize = RDFParser.source("skolemization/skolemized-product.nq").toModel();

		final Model skolemizedModel = new SkolemizedModel(SKOLEM_URI_TEMPLATE, modelToSkolemize).getModel();

		assertThatSkolemizedModel(skolemizedModel).hasNoBlankNodes();
	}
}