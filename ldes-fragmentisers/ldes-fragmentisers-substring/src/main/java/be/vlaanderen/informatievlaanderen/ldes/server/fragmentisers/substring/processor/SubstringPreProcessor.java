package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.processor;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.apicatalog.jsonld.StringUtils;

public class SubstringPreProcessor {

	private final SubstringConfig substringConfig;

	public SubstringPreProcessor(SubstringConfig substringConfig) {
		this.substringConfig = substringConfig;
	}

	public String getSubstringTarget(Member member) {
		return (String) member.getFragmentationObject(substringConfig.getFragmenterSubjectFilter(),
				substringConfig.getFragmenterProperty());
	}

	public List<String> bucketize(String substringTarget) {
		if (StringUtils.isBlank(substringTarget)) {
			return List.of();
		}

		return IntStream
				.rangeClosed(1, substringTarget.length())
				.mapToObj(index -> substringTarget.substring(0, index))
				.toList();
	}

	public List<String> tokenize(String input) {
		if (StringUtils.isBlank(input)) {
			return List.of();
		}

		final String normalizedInput = normalize(input);
		final List<String> tokens = new ArrayList<>(Arrays.asList(normalizedInput.split(" ")));
		if (tokens.size() > 1) {
			// we only add the input if it was actually split, else we end up with a list
			// with duplicate values
			tokens.add(input);
		}
		return tokens;
	}

	private String normalize(String substringTarget) {
		return Normalizer.normalize(substringTarget.trim(), Normalizer.Form.NFKD)
				.replaceAll("\\p{M}", "")
				.replace(",", "")
				.replace("-", " ")
				.toLowerCase();
	}

}
