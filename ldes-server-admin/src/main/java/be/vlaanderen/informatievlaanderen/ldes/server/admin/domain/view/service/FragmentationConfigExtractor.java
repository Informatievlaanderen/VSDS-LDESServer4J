package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.FRAGMENTATION_OBJECT;

@Component
public class FragmentationConfigExtractor {

	private final Function<RDFNode, FragmentationConfig> fragmentationConfigCreationFunction = new FragmentationConfigCreationFunction();

	public List<FragmentationConfig> extractFragmentationConfigs(List<Statement> statements) {
		List<RDFList> fragmentationConfigList = statements.stream()
				.filter(new ConfigFilterPredicate(FRAGMENTATION_OBJECT))
				.map(Statement::getList).toList();
		if (fragmentationConfigList.size() != 1) {
			throw new IllegalArgumentException("Cannot Extract Fragmentation Strategy of view. Expected exactly 1 "
					+ FRAGMENTATION_OBJECT
					+ " statement.\n Found multiple statements in :\n"
					+ RdfModelConverter.toString(ModelFactory.createDefaultModel().add(statements), Lang.TURTLE));
		}
		return fragmentationConfigList
				.get(0)
				.iterator()
				.mapWith(fragmentationConfigCreationFunction)
				.toList();
	}
}
