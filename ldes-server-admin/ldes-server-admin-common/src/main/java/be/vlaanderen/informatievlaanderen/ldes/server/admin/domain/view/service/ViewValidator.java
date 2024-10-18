package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.DuplicateRetentionException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ViewValidator {
	public void validateView(ViewSpecification viewSpecification) {
		checkViewForDuplicateRetentionPolicies(viewSpecification);
	}

	private void checkViewForDuplicateRetentionPolicies(ViewSpecification viewSpecification) {
		List<String> duplicateRetentionPolicies = viewSpecification.getRetentionConfigs().stream()
				.map(retentionPolicy -> retentionPolicy.listObjectsOfProperty(RdfConstants.RDF_SYNTAX_TYPE).nextNode())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.entrySet().stream()
				.filter(entry -> entry.getValue() > 1)
				.map(Map.Entry::getKey)
				.map(Object::toString)
				.toList();

		if (!duplicateRetentionPolicies.isEmpty()) {
			throw new DuplicateRetentionException(duplicateRetentionPolicies);
		}
	}
}
