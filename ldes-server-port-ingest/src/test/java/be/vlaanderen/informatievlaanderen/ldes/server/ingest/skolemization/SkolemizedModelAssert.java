package be.vlaanderen.informatievlaanderen.ldes.server.ingest.skolemization;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.assertj.core.api.AbstractAssert;

public class SkolemizedModelAssert extends AbstractAssert<SkolemizedModelAssert, Model> {

	protected SkolemizedModelAssert(Model model) {
		super(model, SkolemizedModelAssert.class);
	}

	public static SkolemizedModelAssert assertThatSkolemizedModel(Model model) {
		return new SkolemizedModelAssert(model);
	}

	public SkolemizedModelAssert hasNoBlankNodes() {
		final int blankNodesCount = actual.listSubjects().filterKeep(Resource::isAnon).toList().size();
		if(blankNodesCount > 0) {
			failWithMessage("Expected to find no blank nodes, but %d were found", blankNodesCount);
		}
		return this;
	}

	public SkolemizedModelAssert hasSkolemizedObjectsWithPrefix(int count, String prefix) {
		final int actualCount = actual.listObjects()
				.filterKeep(object -> object.toString().contains(prefix))
				.toList()
				.size();

		if(actualCount != count) {
			failWithMessage("Expected to find %d objects with prefix %s, but found %d", count, prefix, actualCount);
		}

		return this;
	}

	public SkolemizedModelAssert hasSkolemizedSubjectsWithPrefix(int count, String prefix) {
		final int actualCount = actual.listSubjects()
				.filterKeep(object -> object.toString().contains(prefix))
				.toList()
				.size();

		if(actualCount != count) {
			failWithMessage("Expected to find %d objects with prefix %s, but found %d", count, prefix, actualCount);
		}

		return this;
	}
}
