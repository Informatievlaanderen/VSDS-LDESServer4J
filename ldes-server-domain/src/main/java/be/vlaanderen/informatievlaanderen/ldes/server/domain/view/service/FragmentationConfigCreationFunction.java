package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
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
	@Override
	public FragmentationConfig apply(RDFNode rdfNode) {
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		List<Statement> statements = rdfNode
				.getModel()
				.listStatements(rdfNode.asResource(), null, (RDFNode) null)
				.toList();
		List<Statement> fragmentationStatements = statements.stream()
				.filter(statement -> statement.getPredicate().equals(RDF_SYNTAX_TYPE)).toList();
		if (fragmentationStatements.size() != 1) {
			throw new IllegalArgumentException(
					"Cannot Create Fragmentation Config. Expected exactly 1 " + FRAGMENTATION_TYPE
							+ " statement.\n Found no or multiple statements in :\n"
							+ RdfModelConverter.toString(ModelFactory.createDefaultModel().add(fragmentationStatements),
							Lang.TURTLE));
		}
		Map<String, String> fragmentationPropertiesMap = statements
				.stream()
				.filter(statement -> !fragmentationStatements.contains(statement))
				.collect(Collectors.toMap(statement -> statement.getPredicate().getLocalName(),
						statement -> {
							if (statement.getObject().isLiteral()) {
								return statement.getObject().asLiteral().toString();
							}
							else {
								return statement.getObject().asNode().toString();
							}
						}));
		String fragmentationName = statements.stream()
				.filter(statement -> statement.getPredicate().equals(RDF_SYNTAX_TYPE))
				.findFirst()
				.orElseThrow()
				.getResource()
				.getLocalName();

		fragmentationConfig.setName(fragmentationName);
		fragmentationConfig.setConfig(fragmentationPropertiesMap);
		return fragmentationConfig;
	}
}
