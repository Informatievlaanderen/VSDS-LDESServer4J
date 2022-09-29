package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shared.PrefixMapping;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PrefixAdderImpl implements PrefixAdder {

	@Override
	public Model addPrefixesToModel(Model model) {
		Map<String, String> nameSpaceMap = new HashMap<>();
		model.listStatements().forEach(statement -> extractNamespaces(nameSpaceMap, statement));
		addNameSpacesAsPrefix(model, nameSpaceMap);
		return model;
	}

	private void extractNamespaces(Map<String, String> nameSpaceMap, Statement statement) {
		addPotentialPrefixToNamespaceMap(nameSpaceMap, statement.getPredicate().getNameSpace());
		if (statement.getObject().isURIResource()) {
			addPotentialPrefixToNamespaceMap(nameSpaceMap, statement.getObject().asResource().getNameSpace());
		}
	}

	private void addPotentialPrefixToNamespaceMap(Map<String, String> nameSpaceMap, String predicateNameSpace) {
		String candidateForPrefix = getPrefixCandidate(predicateNameSpace);
		if (!candidateForPrefix.contains("."))
			nameSpaceMap.put(candidateForPrefix, predicateNameSpace);
	}

	private String getPrefixCandidate(String nameSpace) {
		String[] split = nameSpace.split("/");
		return split[split.length - 1].replace("#", "");
	}

	private void addNameSpacesAsPrefix(Model model, Map<String, String> nameSpaceMap) {
		nameSpaceMap.forEach((prefix, uri) -> {
			try {
				model.setNsPrefix(prefix, uri);
			} catch (PrefixMapping.IllegalPrefixException ignored) {
				// If namespace cannot be added as prefix, ignore.
			}
		});
	}
}
