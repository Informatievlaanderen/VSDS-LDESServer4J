package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.propertypath;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.ArrayList;
import java.util.List;

public class PropertyPathExtractor implements PropertyExtractor {

	private static final String OBJECT_VAR_NAME = "object";
	private static final String IRI_OPENING_SYMBOL = "<";
	private final String queryString;

	private PropertyPathExtractor(String propertyPath) {
		queryString = "SELECT * where { ?subject %s ?object }".formatted(propertyPath);
	}

	/**
	 * This factory method was provided for backwards compatibility.
	 * In the past we supported properties to be provided as strings in a non IRI
	 * format.
	 * When a property is provided as a plain string, we wrap it to an IRI.
	 * NOTE: Does not work with property paths -> ex:foo/ex:bar will not get auto
	 * wrapping.
	 */
	public static PropertyPathExtractor from(String propertyPath) {
		return propertyPath.startsWith(IRI_OPENING_SYMBOL)
				? new PropertyPathExtractor(propertyPath)
				: new PropertyPathExtractor("<%s>".formatted(propertyPath));
	}

	@Override
	public List<RDFNode> getProperties(Model model) {
		final Query query = QueryFactory.create(queryString);
		try (QueryExecution queryExecution = QueryExecutionFactory.create(query, model)) {
			ResultSet resultSet = queryExecution.execSelect();

			List<RDFNode> results = new ArrayList<>();
			while (resultSet.hasNext()) {
				results.add(resultSet.next().get(OBJECT_VAR_NAME));
			}
			return results;
		}
	}

}
