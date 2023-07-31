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

	public Set<Token> getTokens(boolean caseSensitive) {
		return tokenize(caseSensitive)
				.stream()
				.map(Token::new)
				.collect(Collectors.toSet());
	}

	private List<String> tokenize(boolean caseSensitive) {
		if (StringUtils.isBlank(string)) {
			return List.of();
		}

		String normalizedInput = normalize(string);
		if (!caseSensitive) {
			normalizedInput = normalizedInput.toLowerCase();
		}
		return new ArrayList<>(Arrays.asList(normalizedInput.split(" ")));
	}

	private String normalize(String substringTarget) {
		return Normalizer.normalize(substringTarget.trim(), Normalizer.Form.NFKD)
				.replaceAll("\\p{M}", "")
				.replace(",", "")
				.replace("-", " ");
	}
}
