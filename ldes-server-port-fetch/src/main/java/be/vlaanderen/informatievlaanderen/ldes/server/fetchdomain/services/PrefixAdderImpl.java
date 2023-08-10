package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.services;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shared.PrefixMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PrefixAdderImpl implements PrefixAdder {

	private static final String VALID_LOCALNAME_REGEX = "([a-zA-Z][\\-_a-zA-Z0-9]*)$";
	private static final String VALID_PREFIX_REGEX = "^[a-zA-Z_][\\w.-]*$"; // NCName regex
	private static final Logger LOGGER = LoggerFactory.getLogger(PrefixAdderImpl.class);

	@Override
	public Model addPrefixesToModel(Model model) {
		Map<String, String> nameSpaceMap = new HashMap<>();
		Map<String, String> localNamesMap = new HashMap<>();
		model.listStatements().forEach(statement -> extractNamespaces(nameSpaceMap, localNamesMap, statement));
		removePrefixesWithNonCompliantLocalName(nameSpaceMap, localNamesMap);
		addNameSpacesAsPrefix(model, nameSpaceMap);
		return model;
	}

	private void removePrefixesWithNonCompliantLocalName(Map<String, String> nameSpaceMap,
			Map<String, String> localNamesMap) {
		localNamesMap.forEach((localName, prefix) -> {
			if (!localName.matches(VALID_LOCALNAME_REGEX)) {
				nameSpaceMap.remove(prefix);
			}
		});
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
		if (isValidPrefixCandidate(candidateForPrefix)) {
			nameSpaceMap.put(candidateForPrefix, predicateNameSpace);
			localNamesMap.put(localName, candidateForPrefix);
		}
	}

	private static boolean isValidPrefixCandidate(String candidateForPrefix) {
		return !candidateForPrefix.contains(".") && candidateForPrefix.matches(VALID_PREFIX_REGEX);
	}

	private String getPrefixCandidate(String nameSpace) {
		return getStandardPrefix(nameSpace).orElseGet(() -> {
			String[] split = nameSpace.split("/");
			return split[split.length - 1].replace("#", "");
		});
	}

	private Optional<String> getStandardPrefix(String nameSpace) {
		return PrefixMapping.Extended
				.getNsPrefixMap()
				.entrySet()
				.stream()
				.filter(entry -> entry.getValue().equals(nameSpace))
				.map(Map.Entry::getKey)
				.findFirst();
	}

	private void addNameSpacesAsPrefix(Model model, Map<String, String> nameSpaceMap) {
		nameSpaceMap.forEach((prefix, uri) -> {
			try {
				model.setNsPrefix(prefix, uri);
			} catch (PrefixMapping.IllegalPrefixException exception) {
				// If namespace cannot be added as prefix, ignore.
				LOGGER.warn("IllegalPrefixException: {}", exception.getMessage());
			}
		});
	}
}
