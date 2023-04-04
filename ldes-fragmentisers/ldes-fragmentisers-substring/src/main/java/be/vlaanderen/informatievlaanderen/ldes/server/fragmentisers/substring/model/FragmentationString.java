package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.apicatalog.jsonld.StringUtils;

public class FragmentationString {
	private final String string;

	public FragmentationString(final String string) {
		this.string = string;
	}

	public Set<Token> getTokens() {
		return tokenize().stream().map(Token::new).collect(Collectors.toSet());
	}

	private List<String> tokenize() {
		if (StringUtils.isBlank(string)) {
			return List.of();
		}

		final String normalizedInput = normalize(string);
		return new ArrayList<>(Arrays.asList(normalizedInput.split(" ")));
	}

	private String normalize(String substringTarget) {
		return Normalizer.normalize(substringTarget.trim(), Normalizer.Form.NFKD)
				.replaceAll("\\p{M}", "")
				.replace(",", "")
				.replace("-", " ")
				.toLowerCase();
	}
}
