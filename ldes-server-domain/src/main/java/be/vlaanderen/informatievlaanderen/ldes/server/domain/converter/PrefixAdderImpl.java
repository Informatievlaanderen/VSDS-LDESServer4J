package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shared.PrefixMapping;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PrefixAdderImpl implements PrefixAdder {

	private static final String VALID_LOCALNAME_REGEX = "([a-zA-Z][\\-_a-zA-Z0-9]*)$";

	@Override
	public Model addPrefixesToModel(Model model) {
		Map<String, String> nameSpaceMap = new HashMap<>();
		Map<String, String> localNamesMap = new HashMap<>();
		model.listStatements().forEach(statement -> extractNamespaces(nameSpaceMap, localNamesMap, statement));
		localNamesMap.forEach((localName, prefix) -> {
			if (!localName.matches(VALID_LOCALNAME_REGEX)) {
				nameSpaceMap.remove(prefix);
			}
		});
		addNameSpacesAsPrefix(model, nameSpaceMap);
		return model;
	}

	private void extractNamespaces(Map<String, String> nameSpaceMap, Map<String, String> localNamesMap,
			Statement statement) {

		addPotentialPrefixToNamespaceMap(nameSpaceMap, localNamesMap, statement.getPredicate().getNameSpace(),
				statement.getPredicate().getLocalName());
		if (statement.getObject().isURIResource()) {
			addPotentialPrefixToNamespaceMap(nameSpaceMap, localNamesMap,
					statement.getObject().asResource().getNameSpace(),
					statement.getObject().asResource().getLocalName());
		}
	}

	private void addPotentialPrefixToNamespaceMap(Map<String, String> nameSpaceMap, Map<String, String> localNamesMap,
			String predicateNameSpace, String localName) {
		String candidateForPrefix = getPrefixCandidate(predicateNameSpace);
		if (!candidateForPrefix.contains(".")) {
			nameSpaceMap.put(candidateForPrefix, predicateNameSpace);
			localNamesMap.put(localName, candidateForPrefix);
		}
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
