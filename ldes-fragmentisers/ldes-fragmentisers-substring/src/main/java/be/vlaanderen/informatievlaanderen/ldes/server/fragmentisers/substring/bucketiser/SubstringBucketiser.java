package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.bucketiser;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;

import java.text.Normalizer;
import java.util.List;
import java.util.stream.IntStream;

public class SubstringBucketiser {
	private final SubstringConfig substringConfig;

	public SubstringBucketiser(SubstringConfig substringConfig) {
		this.substringConfig = substringConfig;
	}

	public List<String> bucketise(LdesMember member) {
		String substringTarget = (String) member.getFragmentationObject(substringConfig.getFragmenterSubjectFilter(),
				substringConfig.getFragmenterProperty());
		String normalizedSubstringTarget = normalize(substringTarget);
		return createSubstringSet(normalizedSubstringTarget);
	}

	private String normalize(String substringTarget) {
		return Normalizer.normalize(substringTarget.trim(), Normalizer.Form.NFKD)
				.replaceAll("\\p{M}", "")
				.replace(",", "")
				.replace("-", " ")
				.toLowerCase();
	}

	private List<String> createSubstringSet(String substringTarget) {
		return IntStream
				.rangeClosed(1, substringTarget.length())
				.mapToObj(index -> substringTarget.substring(0, index))
				.toList();
	}
}
