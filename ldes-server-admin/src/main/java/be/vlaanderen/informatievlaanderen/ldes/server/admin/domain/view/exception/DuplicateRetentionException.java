package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class DuplicateRetentionException extends RuntimeException {
	private final List<String> duplicateRetentionPolicies;

	public DuplicateRetentionException() {
		this(List.of());
	}

	public DuplicateRetentionException(@NotNull List<String> duplicateRetentionPolicies) {
		this.duplicateRetentionPolicies = duplicateRetentionPolicies;
	}

	@Override
	public String getMessage() {
		if(duplicateRetentionPolicies.isEmpty()) {
			return "More then one retention policy of the same type found";
		}
		var formattedDuplicates = duplicateRetentionPolicies.stream().map("<%s>"::formatted).collect(Collectors.joining(", "));
		return "More then one retention policy of type %s found".formatted(formattedDuplicates);
	}
}
