package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.processor;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.apicatalog.jsonld.StringUtils;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.SubstringConstants.ROOT_SUBSTRING;

public class SubstringPreProcessor {

	private final SubstringConfig substringConfig;

	public SubstringPreProcessor(SubstringConfig substringConfig) {
		this.substringConfig = substringConfig;
	}

	public String getSubstringTarget(Member member) {
		return (String) member.getFragmentationObject(substringConfig.getFragmenterSubjectFilter(),
				substringConfig.getFragmenterProperty());
	}

    // TODO: 20/02/2023 fix testing
	public List<String> bucketize(String substringTarget) {
		final List<String> bucket = new ArrayList<>(List.of(ROOT_SUBSTRING));
		if (StringUtils.isBlank(substringTarget)) {
			return bucket;
		}

		bucket.addAll(
				IntStream
						.rangeClosed(1, substringTarget.length())
						.mapToObj(index -> substringTarget.substring(0, index))
						.toList());

		return bucket;
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
			tokens.add(normalizedInput);
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
