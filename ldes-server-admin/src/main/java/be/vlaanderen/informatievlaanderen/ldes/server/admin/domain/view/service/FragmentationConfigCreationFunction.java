package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.ModelToViewConverterException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.FRAGMENTATION_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;

public class FragmentationConfigCreationFunction implements Function<RDFNode, FragmentationConfig> {

	public static final String PAGINATION_FRAGMENTATION = "PaginationFragmentation";

	@Override
	public FragmentationConfig apply(RDFNode rdfNode) {
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		List<Statement> statements = rdfNode
				.getModel()
				.listStatements(rdfNode.asResource(), null, (RDFNode) null)
				.toList();
		List<Statement> fragmentationStatements = statements.stream()
				.filter(statement -> statement.getPredicate().equals(RDF_SYNTAX_TYPE)).toList();
		validateFragmentationStatements(fragmentationStatements);
		Map<String, String> fragmentationPropertiesMap = extractFragmentationProperties(statements,
				fragmentationStatements);
		String fragmentationName = computeFragmentationName(statements);

		fragmentationConfig.setName(fragmentationName);
		fragmentationConfig.setConfig(fragmentationPropertiesMap);
		return fragmentationConfig;
	}

	private static void validateFragmentationStatements(List<Statement> fragmentationStatements) {
		if (fragmentationStatements.size() != 1) {
			throw new IllegalArgumentException(
					"Cannot Create Fragmentation Config. Expected exactly 1 " + FRAGMENTATION_TYPE
							+ " statement.\n Found no or multiple statements in :\n"
							+ RdfModelConverter.toString(ModelFactory.createDefaultModel().add(fragmentationStatements),
									Lang.TURTLE));
		}
	}

	private Map<String, String> extractFragmentationProperties(List<Statement> statements,
			List<Statement> fragmentationStatements) {
		return statements.stream()
				.filter(statement -> !fragmentationStatements.contains(statement))
				.collect(Collectors.toMap(statement -> statement.getPredicate().getLocalName(),
						statement -> {
							if (statement.getObject().isLiteral()) {
								return statement.getObject().asLiteral().getValue().toString();
							} else {
								return statement.getObject().asNode().toString();
							}
						}));
	}

	private String computeFragmentationName(List<Statement> statements) {
		final String fragmentationName = statements.stream()
				.filter(statement -> statement.getPredicate().equals(RDF_SYNTAX_TYPE))
				.findFirst()
				.orElseThrow(() -> new ModelToViewConverterException("Unable to find fragmentation type"))
				.getResource()
				.getLocalName();

		if (PAGINATION_FRAGMENTATION.equals(fragmentationName)) {
			throw new IllegalArgumentException("Pagination cannot be chosen as fragmentation strategy." +
					"We paginate every view by default. " +
					"To create a view that only has pagination, create it without fragmentation strategies.");
		}

		return fragmentationName;
	}
}
